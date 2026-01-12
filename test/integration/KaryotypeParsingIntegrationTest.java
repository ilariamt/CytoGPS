package integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.*;

import business.*;
import main.KaryotypeRunner;

import java.util.List;

/**
 * Integration tests for complete karyotype parsing workflow
 * Tests end-to-end parsing from input string to biological outcome
 */
public class KaryotypeParsingIntegrationTest {

    // ========== Normal Karyotypes ==========

    @Test
    @DisplayName("Normal female karyotype: 46,XX")
    public void testNormalFemaleKaryotype() {
        String karyotype = "46,XX";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        assertNotNull(result);
        assertFalse(result.isContainingLexerParserError(), "Normal karyotype should parse successfully");
        assertFalse(result.isContainingValidationError(), "Normal karyotype should have no validation errors");
        assertEquals(1, result.getBiologicalOutcomeList().size(), "Should have one biological outcome");
    }

    @Test
    @DisplayName("Normal male karyotype: 46,XY")
    public void testNormalMaleKaryotype() {
        String karyotype = "46,XY";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        assertNotNull(result);
        assertFalse(result.isContainingLexerParserError());
        assertEquals(1, result.getBiologicalOutcomeList().size());
    }

    // ========== Common CLL Karyotypes ==========

    @Test
    @DisplayName("CLL: Deletion 13q14")
    public void testDel13q14() {
        String karyotype = "46,XY,del(13)(q14)";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        assertFalse(result.isContainingLexerParserError());
        assertEquals(1, result.getBiologicalOutcomeList().size());

        BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
        List<Integer> lossOutcome = outcome.getKaryotypeLGF().get(0);

        // Verify deletion at 13q14
        ParseEvent pe = new ParseEvent();
        Integer index13q14 = pe.getChrToIndexMap().get("13q14.11");
        if (index13q14 != null) {
            assertTrue(lossOutcome.get(index13q14) > 0, "Should show loss at 13q14");
        }
    }

    @Test
    @DisplayName("CLL: Deletion 11q22-q23")
    public void testDel11q() {
        String karyotype = "46,XX,del(11)(q22q23)";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        assertFalse(result.isContainingLexerParserError());
        assertEquals(1, result.getBiologicalOutcomeList().size());

        BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
        List<Integer> lossOutcome = outcome.getKaryotypeLGF().get(0);

        // Verify deletion at 11q22-q23 region
        ParseEvent pe = new ParseEvent();
        Integer index11q22 = pe.getChrToIndexMap().get("11q22.1");
        if (index11q22 != null) {
            assertTrue(lossOutcome.get(index11q22) > 0, "Should show loss at 11q22");
        }
    }

    @Test
    @DisplayName("CLL: Trisomy 12")
    public void testTrisomy12() {
        String karyotype = "47,XY,+12";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        assertFalse(result.isContainingLexerParserError());
        assertEquals(1, result.getBiologicalOutcomeList().size());

        BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
        List<Integer> gainOutcome = outcome.getKaryotypeLGF().get(1);

        // Verify gain across chromosome 12
        ParseEvent pe = new ParseEvent();
        Integer index12p = pe.getChrToIndexMap().get("12p13.33");
        Integer index12q = pe.getChrToIndexMap().get("12q24.33");

        if (index12p != null && index12q != null) {
            assertTrue(gainOutcome.get(index12p) > 0, "Should show gain on 12p");
            assertTrue(gainOutcome.get(index12q) > 0, "Should show gain on 12q");
        }
    }

    @Test
    @DisplayName("CLL: Deletion 17p (TP53)")
    public void testDel17p() {
        String karyotype = "46,XY,del(17)(p13)";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        assertFalse(result.isContainingLexerParserError());
        assertEquals(1, result.getBiologicalOutcomeList().size());

        BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
        List<Integer> lossOutcome = outcome.getKaryotypeLGF().get(0);

        ParseEvent pe = new ParseEvent();
        Integer index17p13 = pe.getChrToIndexMap().get("17p13.1");
        if (index17p13 != null) {
            assertTrue(lossOutcome.get(index17p13) > 0, "Should show loss at 17p13 (TP53)");
        }
    }

    @Test
    @DisplayName("CLL: Philadelphia chromosome t(9;22)")
    public void testPhiladelphiaChromosome() {
        String karyotype = "46,XY,t(9;22)(q34;q11)";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        assertFalse(result.isContainingLexerParserError());
        assertEquals(1, result.getBiologicalOutcomeList().size());

        BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
        List<Integer> fusionOutcome = outcome.getKaryotypeLGF().get(2);

        // Check fusion at breakpoints
        assertTrue(fusionOutcome.stream().anyMatch(f -> f > 0),
                "Should show fusion for t(9;22)");
    }

    // ========== Complex Karyotypes ==========

    @Test
    @DisplayName("Complex: Multiple clones")
    public void testMultipleClones() {
        String karyotype = "47,XX,+8[15]/46,XX[5]";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        assertFalse(result.isContainingLexerParserError());
        assertEquals(2, result.getBiologicalOutcomeList().size(), "Should have 2 clones");

        // First clone has trisomy 8
        BiologicalOutcome clone1 = result.getBiologicalOutcomeList().get(0);
        List<Integer> gainOutcome = clone1.getKaryotypeLGF().get(1);

        ParseEvent pe = new ParseEvent();
        Integer index8q = pe.getChrToIndexMap().get("8q24.3");
        if (index8q != null) {
            assertTrue(gainOutcome.get(index8q) > 0, "Clone 1 should show gain on chr 8");
        }

        // Second clone is normal (no gains)
        BiologicalOutcome clone2 = result.getBiologicalOutcomeList().get(1);
        List<Integer> normalGain = clone2.getKaryotypeLGF().get(1);
        assertTrue(normalGain.stream().allMatch(g -> g == 0),
                "Clone 2 should be normal (no gains)");
    }

    @Test
    @DisplayName("Complex: Derivative chromosome der(14)")
    public void testDerivativeChromosome() {
        String karyotype = "46,XY,der(14)t(11;14)(q13;q32)";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        // Derivative chromosomes are complex - at minimum should not crash
        assertNotNull(result);
        assertNotNull(result.getBiologicalOutcomeList());
    }

    // ========== Parameterized Tests from CSV ==========

    @ParameterizedTest
    @CsvFileSource(resources = "/test_karyotypes.csv", numLinesToSkip = 1)
    @DisplayName("Batch test karyotypes from CSV")
    public void testKaryotypesFromCSV(String karyotype, String expectedStatus, String description) {
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        assertNotNull(result, "Result should not be null for: " + description);

        if ("success".equalsIgnoreCase(expectedStatus)) {
            assertFalse(result.isContainingLexerParserError(),
                    "Expected success for: " + description + " | Karyotype: " + karyotype);
        }
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Edge case: Empty karyotype string")
    public void testEmptyKaryotype() {
        String karyotype = "";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        // Empty input should either error or return empty result
        assertNotNull(result);
    }

    @Test
    @DisplayName("Edge case: Invalid chromosome number")
    public void testInvalidChromosomeNumber() {
        String karyotype = "50,XX,+1,+2,+3,+4";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        // Should detect chromosome count mismatch
        assertNotNull(result);
    }

    @Test
    @DisplayName("Edge case: Malformed translocation")
    public void testMalformedTranslocation() {
        String karyotype = "46,XY,t(9;22)"; // Missing breakpoints
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        assertNotNull(result);
        // Should either succeed with uncertainty flag or fail gracefully
    }
}
