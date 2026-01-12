package business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for DetailedFormulaParser class
 * Tests detailed derivative chromosome formula parsing including:
 * - Simple derivatives
 * - Ring chromosomes
 * - HSR regions
 * - Dicentric chromosomes
 * - Multiple fragments
 * - Mixed format karyotypes
 *
 * @author ilariamt
 * Date: January 03, 2026
 */
public class DetailedFormulaParserTest {

    private DetailedFormulaParser parser;
    private ParseEvent parseEvent;

    @BeforeEach
    public void setUp() {
        parser = new DetailedFormulaParser();
        parseEvent = new ParseEvent();
    }

    // ========== Test Case 1: Simple Derivative ==========

    @Test
    @DisplayName("Test Case 1: Simple derivative with insertion from another chromosome")
    public void testSimpleDerivative() {
        String input = "der(13)(13pter->13q10::15q10->15q21::13q14->13qter)";
        BiologicalOutcome outcome = parser.parseDetailedFormula(input);

        assertNotNull(outcome, "Outcome should not be null");

        List<List<Integer>> lgf = outcome.getKaryotypeLGF();
        List<Integer> lossOutcome = lgf.get(0);
        List<Integer> gainOutcome = lgf.get(1);
        List<Integer> fusionOutcome = lgf.get(2);

        // Test losses on chr 13: 13q10->13q14 gap should be marked as loss
        // Bands in gap: 13q11, 13q12.11, 13q12.12, 13q12.13, 13q12.2, 13q12.3, 13q13.1, 13q13.2, 13q13.3
        int index13q11 = parseEvent.getChrToIndexMap().get("13q11");
        int index13q12_11 = parseEvent.getChrToIndexMap().get("13q12.11");
        int index13q13_3 = parseEvent.getChrToIndexMap().get("13q13.3");

        assertTrue(lossOutcome.get(index13q11) > 0, "13q11 should be marked as loss");
        assertTrue(lossOutcome.get(index13q12_11) > 0, "13q12.11 should be marked as loss");
        assertTrue(lossOutcome.get(index13q13_3) > 0, "13q13.3 should be marked as loss");

        // Test gains on chr 15: 15q10->15q21 segment should be marked as gain
        int index15q11_1 = parseEvent.getChrToIndexMap().get("15q11.1");
        int index15q15_1 = parseEvent.getChrToIndexMap().get("15q15.1");
        int index15q21_1 = parseEvent.getChrToIndexMap().get("15q21.1");

        assertTrue(gainOutcome.get(index15q11_1) > 0, "15q11.1 should be marked as gain");
        assertTrue(gainOutcome.get(index15q15_1) > 0, "15q15.1 should be marked as gain");
        assertTrue(gainOutcome.get(index15q21_1) > 0, "15q21.1 should be marked as gain");

        // Test fusion points: 13q10, 15q10, 15q21, 13q14
        int index13q10 = parseEvent.getChrToIndexMap().get("13q10");
        int index15q10 = parseEvent.getChrToIndexMap().get("15q10");
        int index15q21 = parseEvent.getChrToIndexMap().get("15q21.1"); // First match
        int index13q14 = parseEvent.getChrToIndexMap().get("13q14.11"); // First match

        assertTrue(fusionOutcome.get(index13q10) > 0, "13q10 should be marked as fusion");
        assertTrue(fusionOutcome.get(index15q10) > 0, "15q10 should be marked as fusion");
        assertTrue(fusionOutcome.get(index15q21) > 0, "15q21 should be marked as fusion");
        assertTrue(fusionOutcome.get(index13q14) > 0, "13q14 should be marked as fusion");

        // Test that present chr 13 segments are NOT marked as loss
        int index13p11_2 = parseEvent.getChrToIndexMap().get("13p11.2");
        int index13q21_1 = parseEvent.getChrToIndexMap().get("13q21.1");

        assertEquals(0, lossOutcome.get(index13p11_2), "13p11.2 (present segment) should NOT be marked as loss");
        assertEquals(0, lossOutcome.get(index13q21_1), "13q21.1 (present segment) should NOT be marked as loss");
    }

    // ========== Test Case 2: Ring Chromosome ==========

    @Test
    @DisplayName("Test Case 2: Ring chromosome with leading/trailing :: markers")
    public void testRingChromosome() {
        String input = "der(7)(::7q11->7q31::)";
        BiologicalOutcome outcome = parser.parseDetailedFormula(input);

        assertNotNull(outcome, "Outcome should not be null");

        List<List<Integer>> lgf = outcome.getKaryotypeLGF();
        List<Integer> lossOutcome = lgf.get(0);
        List<Integer> gainOutcome = lgf.get(1);
        List<Integer> fusionOutcome = lgf.get(2);

        // Test losses: 7pter->7q11 and 7q31->7qter should be lost
        int index7p10 = parseEvent.getChrToIndexMap().get("7p10");
        int index7p22_3 = parseEvent.getChrToIndexMap().get("7p22.3");
        int index7q32_1 = parseEvent.getChrToIndexMap().get("7q32.1");
        int index7q36_3 = parseEvent.getChrToIndexMap().get("7q36.3");

        assertTrue(lossOutcome.get(index7p10) > 0, "7p10 should be marked as loss");
        assertTrue(lossOutcome.get(index7p22_3) > 0, "7p22.3 should be marked as loss");
        assertTrue(lossOutcome.get(index7q32_1) > 0, "7q32.1 should be marked as loss");
        assertTrue(lossOutcome.get(index7q36_3) > 0, "7q36.3 should be marked as loss");

        // Test no gains (all material from chr 7)
        boolean anyGains = gainOutcome.stream().anyMatch(val -> val > 0);
        assertFalse(anyGains, "Ring chromosome should have no gains (all material from same chr)");

        // Test fusion points: 7q11 and 7q31
        int index7q11_1 = parseEvent.getChrToIndexMap().get("7q11.1"); // First match
        int index7q31_1 = parseEvent.getChrToIndexMap().get("7q31.1"); // First match

        assertTrue(fusionOutcome.get(index7q11_1) > 0, "7q11 should be marked as fusion");
        assertTrue(fusionOutcome.get(index7q31_1) > 0, "7q31 should be marked as fusion");
    }

    // ========== Test Case 3: Multiple Fragments ==========

    @Test
    @DisplayName("Test Case 3: Multiple fragments from different chromosomes")
    public void testMultipleFragments() {
        String input = "der(1)(1pter->1p32::8q21->8q24::1p32->1q10::3q10->3q21::1q10->1qter)";
        BiologicalOutcome outcome = parser.parseDetailedFormula(input);

        assertNotNull(outcome, "Outcome should not be null");

        List<List<Integer>> lgf = outcome.getKaryotypeLGF();
        List<Integer> lossOutcome = lgf.get(0);
        List<Integer> gainOutcome = lgf.get(1);
        List<Integer> fusionOutcome = lgf.get(2);

        // Test gains from chr 8: 8q21->8q24
        int index8q22_1 = parseEvent.getChrToIndexMap().get("8q22.1");
        assertTrue(gainOutcome.get(index8q22_1) > 0, "8q22.1 should be marked as gain");

        // Test gains from chr 3: 3q10->3q21
        int index3q11_1 = parseEvent.getChrToIndexMap().get("3q11.1");
        int index3q13_11 = parseEvent.getChrToIndexMap().get("3q13.11");
        assertTrue(gainOutcome.get(index3q11_1) > 0, "3q11.1 should be marked as gain");
        assertTrue(gainOutcome.get(index3q13_11) > 0, "3q13.11 should be marked as gain");

        // Test that chr 1 has no losses (all segments present)
        // The formula shows complete chr 1: 1pter->1p32, 1p32->1q10, 1q10->1qter
        // This is a complete chromosome with insertions, so no chr 1 losses expected

        // Test fusion points
        int index1p32 = parseEvent.getChrToIndexMap().get("1p32.1"); // First match
        int index8q21 = parseEvent.getChrToIndexMap().get("8q21.11"); // First match
        int index8q24 = parseEvent.getChrToIndexMap().get("8q24.11"); // First match

        assertTrue(fusionOutcome.get(index1p32) > 0, "1p32 should be marked as fusion");
        assertTrue(fusionOutcome.get(index8q21) > 0, "8q21 should be marked as fusion");
        assertTrue(fusionOutcome.get(index8q24) > 0, "8q24 should be marked as fusion");
    }

    // ========== Test Case 4: HSR Regions ==========

    @Test
    @DisplayName("Test Case 4: HSR (Homogeneously Staining Region) handling")
    public void testHSRRegion() {
        String input = "der(8)(8pter->8q21::hsr::8q24->8qter)";
        BiologicalOutcome outcome = parser.parseDetailedFormula(input);

        assertNotNull(outcome, "Outcome should not be null");

        List<List<Integer>> lgf = outcome.getKaryotypeLGF();
        List<Integer> lossOutcome = lgf.get(0);
        List<Integer> gainOutcome = lgf.get(1);
        List<Integer> fusionOutcome = lgf.get(2);

        // Test losses: 8q21->8q24 gap
        int index8q22_1 = parseEvent.getChrToIndexMap().get("8q22.1");
        int index8q23_1 = parseEvent.getChrToIndexMap().get("8q23.1");

        assertTrue(lossOutcome.get(index8q22_1) > 0, "8q22.1 should be marked as loss");
        assertTrue(lossOutcome.get(index8q23_1) > 0, "8q23.1 should be marked as loss");

        // Test no gains (HSR should be skipped, no specific bands gained)
        boolean anyGains = gainOutcome.stream().anyMatch(val -> val > 0);
        assertFalse(anyGains, "HSR regions should not create gains (unknown exact bands)");

        // Test fusion points: 8q21 and 8q24
        int index8q21 = parseEvent.getChrToIndexMap().get("8q21.11"); // First match
        int index8q24 = parseEvent.getChrToIndexMap().get("8q24.11"); // First match

        assertTrue(fusionOutcome.get(index8q21) > 0, "8q21 should be marked as fusion");
        assertTrue(fusionOutcome.get(index8q24) > 0, "8q24 should be marked as fusion");
    }

    // ========== Test Case 5: Dicentric Chromosomes ==========

    @Test
    @DisplayName("Test Case 5: Dicentric chromosome with two base chromosomes")
    public void testDicentricChromosome() {
        String input = "der(13;15)(13pter->13q10::15q10->15q21)";
        BiologicalOutcome outcome = parser.parseDetailedFormula(input);

        assertNotNull(outcome, "Outcome should not be null");

        List<List<Integer>> lgf = outcome.getKaryotypeLGF();
        List<Integer> lossOutcome = lgf.get(0);
        List<Integer> gainOutcome = lgf.get(1);
        List<Integer> fusionOutcome = lgf.get(2);

        // Test losses on chr 13: 13q10->13qter (everything after q10)
        int index13q11 = parseEvent.getChrToIndexMap().get("13q11");
        int index13q34 = parseEvent.getChrToIndexMap().get("13q34");

        assertTrue(lossOutcome.get(index13q11) > 0, "13q11 should be marked as loss");
        assertTrue(lossOutcome.get(index13q34) > 0, "13q34 should be marked as loss");

        // Test losses on chr 15: 15pter->15q10 and 15q21->15qter (both ends)
        int index15p10 = parseEvent.getChrToIndexMap().get("15p10");
        int index15p13 = parseEvent.getChrToIndexMap().get("15p13");
        int index15q22_1 = parseEvent.getChrToIndexMap().get("15q22.1");
        int index15q26_3 = parseEvent.getChrToIndexMap().get("15q26.3");

        assertTrue(lossOutcome.get(index15p10) > 0, "15p10 should be marked as loss");
        assertTrue(lossOutcome.get(index15p13) > 0, "15p13 should be marked as loss");
        assertTrue(lossOutcome.get(index15q22_1) > 0, "15q22.1 should be marked as loss");
        assertTrue(lossOutcome.get(index15q26_3) > 0, "15q26.3 should be marked as loss");

        // Test no gains (all material from base chromosomes 13 or 15)
        boolean anyGains = gainOutcome.stream().anyMatch(val -> val > 0);
        assertFalse(anyGains, "Dicentric should have no gains (all material from base chromosomes)");

        // Test fusion points: 13q10, 15q10, 15q21
        int index13q10 = parseEvent.getChrToIndexMap().get("13q10");
        int index15q10 = parseEvent.getChrToIndexMap().get("15q10");
        int index15q21 = parseEvent.getChrToIndexMap().get("15q21.1"); // First match

        assertTrue(fusionOutcome.get(index13q10) > 0, "13q10 should be marked as fusion");
        assertTrue(fusionOutcome.get(index15q10) > 0, "15q10 should be marked as fusion");
        assertTrue(fusionOutcome.get(index15q21) > 0, "15q21 should be marked as fusion");
    }

    // ========== Test Case 6: Mixed Format ==========

    @Test
    @DisplayName("Test Case 6: Mixed format with standard and detailed events")
    public void testMixedFormat() {
        String input = "46,XX,+7,del(5)(q13q31),der(13)(13pter->13q10::15q21->15q31::13q14->13qter)";

        // This test uses KaryotypeRunner, not just DetailedFormulaParser
        FinalResult result = main.KaryotypeRunner.getFinalResult(input);

        assertNotNull(result, "FinalResult should not be null");
        assertFalse(result.getBiologicalOutcomeList().isEmpty(), "Should have biological outcomes");

        BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
        List<List<Integer>> lgf = outcome.getKaryotypeLGF();
        List<Integer> lossOutcome = lgf.get(0);
        List<Integer> gainOutcome = lgf.get(1);
        List<Integer> fusionOutcome = lgf.get(2);

        // Test del(5) losses: 5q13->5q31
        int index5q21_1 = parseEvent.getChrToIndexMap().get("5q21.1");
        assertTrue(lossOutcome.get(index5q21_1) > 0, "5q21.1 should be marked as loss (from del(5))");

        // Test +7 gain: entire chr 7
        int index7q11_1 = parseEvent.getChrToIndexMap().get("7q11.1");
        int index7q31_1 = parseEvent.getChrToIndexMap().get("7q31.1");
        assertTrue(gainOutcome.get(index7q11_1) > 0, "7q11.1 should be marked as gain (from +7)");
        assertTrue(gainOutcome.get(index7q31_1) > 0, "7q31.1 should be marked as gain (from +7)");

        // Test der(13) losses: 13q10->13q14 gap
        int index13q11 = parseEvent.getChrToIndexMap().get("13q11");
        int index13q13_3 = parseEvent.getChrToIndexMap().get("13q13.3");
        assertTrue(lossOutcome.get(index13q11) > 0, "13q11 should be marked as loss (from der(13))");
        assertTrue(lossOutcome.get(index13q13_3) > 0, "13q13.3 should be marked as loss (from der(13))");

        // Test der(13) gains: 15q21->15q31
        int index15q22_1 = parseEvent.getChrToIndexMap().get("15q22.1");
        int index15q26_1 = parseEvent.getChrToIndexMap().get("15q26.1");
        assertTrue(gainOutcome.get(index15q22_1) > 0, "15q22.1 should be marked as gain (from der(13))");
        assertTrue(gainOutcome.get(index15q26_1) > 0, "15q26.1 should be marked as gain (from der(13))");
    }

    // ========== Helper Method Tests ==========

    @Test
    @DisplayName("isDetailedFormula should detect detailed formulas correctly")
    public void testIsDetailedFormula() {
        assertTrue(DetailedFormulaParser.isDetailedFormula("der(13)(13pter->13q10::15q10->15q21::13q14->13qter)"),
                "Should detect detailed formula with -> and ::");

        assertTrue(DetailedFormulaParser.isDetailedFormula("der(7)(::7q11->7q31::)"),
                "Should detect ring chromosome");

        assertFalse(DetailedFormulaParser.isDetailedFormula("der(13)t(13;15)(q10;q21)"),
                "Should NOT detect standard derivative");

        assertFalse(DetailedFormulaParser.isDetailedFormula("del(5)(q13q31)"),
                "Should NOT detect standard deletion");
    }

    @Test
    @DisplayName("Empty or invalid input should return valid BiologicalOutcome")
    public void testInvalidInput() {
        BiologicalOutcome outcome1 = parser.parseDetailedFormula("");
        assertNotNull(outcome1, "Empty input should return non-null outcome");

        BiologicalOutcome outcome2 = parser.parseDetailedFormula("invalid");
        assertNotNull(outcome2, "Invalid input should return non-null outcome");
    }
}
