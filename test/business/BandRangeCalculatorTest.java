package business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for BandRangeCalculator utility class
 * Tests chromosome band range calculation methods
 *
 * @author ilariamt
 * Date: January 03, 2026
 */
public class BandRangeCalculatorTest {

    private ParseEvent parseEvent;

    @BeforeEach
    public void setUp() {
        parseEvent = new ParseEvent();
    }

    // ========== getAllBandsBetween Tests ==========

    @Test
    @DisplayName("getAllBandsBetween should return all bands in simple range")
    public void testGetAllBandsBetweenSimple() {
        List<String> bands = BandRangeCalculator.getAllBandsBetween("15q10", "15q21");

        assertNotNull(bands, "Band list should not be null");
        assertFalse(bands.isEmpty(), "Band list should not be empty");

        // Should include starting point
        assertTrue(bands.contains("15q10"), "Should include starting band 15q10");

        // Should include intermediate bands
        assertTrue(bands.contains("15q11.1"), "Should include 15q11.1");
        assertTrue(bands.contains("15q11.2"), "Should include 15q11.2");
        assertTrue(bands.contains("15q12"), "Should include 15q12");
        assertTrue(bands.contains("15q13.1"), "Should include 15q13.1");
        assertTrue(bands.contains("15q15.1"), "Should include 15q15.1");

        // Should include ending point (at least one sub-band)
        boolean hasQ21 = bands.stream().anyMatch(b -> b.startsWith("15q21"));
        assertTrue(hasQ21, "Should include 15q21.x bands");
    }

    @Test
    @DisplayName("getAllBandsBetween should handle pter (p telomere)")
    public void testGetAllBandsBetweenWithPter() {
        List<String> bands = BandRangeCalculator.getAllBandsBetween("13pter", "13p11");

        assertNotNull(bands, "Band list should not be null");
        assertFalse(bands.isEmpty(), "Band list should not be empty");

        // Should include centromere and p arm bands
        assertTrue(bands.contains("13p10"), "Should include centromere 13p10");
        assertTrue(bands.stream().anyMatch(b -> b.startsWith("13p11")),
                "Should include 13p11.x bands");
    }

    @Test
    @DisplayName("getAllBandsBetween should handle qter (q telomere)")
    public void testGetAllBandsBetweenWithQter() {
        List<String> bands = BandRangeCalculator.getAllBandsBetween("13q31", "13qter");

        assertNotNull(bands, "Band list should not be null");
        assertFalse(bands.isEmpty(), "Band list should not be empty");

        // Should include bands from 13q31 to end of q arm
        assertTrue(bands.stream().anyMatch(b -> b.startsWith("13q31")),
                "Should include 13q31.x bands");
        assertTrue(bands.stream().anyMatch(b -> b.startsWith("13q32")),
                "Should include 13q32.x bands");
        assertTrue(bands.stream().anyMatch(b -> b.startsWith("13q34")),
                "Should include 13q34 (telomere)");
    }

    @Test
    @DisplayName("getAllBandsBetween should handle reversed order (swap start/end)")
    public void testGetAllBandsBetweenReversed() {
        // Test with reversed order (end before start)
        List<String> bands = BandRangeCalculator.getAllBandsBetween("15q21", "15q10");

        assertNotNull(bands, "Band list should not be null");
        assertFalse(bands.isEmpty(), "Band list should not be empty");

        // Should still return the range (method swaps them internally)
        assertTrue(bands.contains("15q10"), "Should include 15q10");
        assertTrue(bands.stream().anyMatch(b -> b.startsWith("15q11")),
                "Should include 15q11.x bands");
    }

    @Test
    @DisplayName("getAllBandsBetween should handle sub-band precision")
    public void testGetAllBandsBetweenSubBands() {
        List<String> bands = BandRangeCalculator.getAllBandsBetween("15q21.1", "15q21.3");

        assertNotNull(bands, "Band list should not be null");
        assertFalse(bands.isEmpty(), "Band list should not be empty");

        assertTrue(bands.contains("15q21.1"), "Should include 15q21.1");
        assertTrue(bands.contains("15q21.2"), "Should include 15q21.2");
        assertTrue(bands.contains("15q21.3"), "Should include 15q21.3");
    }

    @Test
    @DisplayName("getAllBandsBetween should return empty list for invalid bands")
    public void testGetAllBandsBetweenInvalid() {
        List<String> bands = BandRangeCalculator.getAllBandsBetween("99p99", "99q99");

        assertNotNull(bands, "Band list should not be null");
        assertTrue(bands.isEmpty(), "Invalid bands should return empty list");
    }

    @Test
    @DisplayName("getAllBandsBetween should handle same start and end")
    public void testGetAllBandsBetweenSameBand() {
        List<String> bands = BandRangeCalculator.getAllBandsBetween("15q21", "15q21");

        assertNotNull(bands, "Band list should not be null");
        assertFalse(bands.isEmpty(), "Same band should return at least one match");

        // Should include the band itself (or its sub-bands)
        boolean hasQ21 = bands.stream().anyMatch(b -> b.startsWith("15q21"));
        assertTrue(hasQ21, "Should include 15q21.x bands");
    }

    // ========== getAllBandsInArm Tests ==========

    @Test
    @DisplayName("getAllBandsInArm should return all p arm bands")
    public void testGetAllBandsInPArm() {
        List<String> bands = BandRangeCalculator.getAllBandsInArm("13p");

        assertNotNull(bands, "Band list should not be null");
        assertFalse(bands.isEmpty(), "P arm should have bands");

        // All bands should start with 13p
        for (String band : bands) {
            assertTrue(band.startsWith("13p"), "All bands should be on 13p: " + band);
        }

        // Should include centromere
        assertTrue(bands.contains("13p10"), "Should include centromere 13p10");

        // Should include distal p bands
        assertTrue(bands.stream().anyMatch(b -> b.startsWith("13p11")),
                "Should include 13p11.x bands");
        assertTrue(bands.stream().anyMatch(b -> b.startsWith("13p13")),
                "Should include 13p13 (telomere)");
    }

    @Test
    @DisplayName("getAllBandsInArm should return all q arm bands")
    public void testGetAllBandsInQArm() {
        List<String> bands = BandRangeCalculator.getAllBandsInArm("13q");

        assertNotNull(bands, "Band list should not be null");
        assertFalse(bands.isEmpty(), "Q arm should have bands");

        // All bands should start with 13q
        for (String band : bands) {
            assertTrue(band.startsWith("13q"), "All bands should be on 13q: " + band);
        }

        // Should include centromere
        assertTrue(bands.contains("13q10"), "Should include centromere 13q10");

        // Should include distal q bands
        assertTrue(bands.stream().anyMatch(b -> b.startsWith("13q11")),
                "Should include 13q11 bands");
        assertTrue(bands.stream().anyMatch(b -> b.startsWith("13q34")),
                "Should include 13q34 (telomere)");
    }

    @Test
    @DisplayName("getAllBandsInArm should handle X chromosome")
    public void testGetAllBandsInArmXChromosome() {
        List<String> bands = BandRangeCalculator.getAllBandsInArm("xq");

        assertNotNull(bands, "Band list should not be null");
        // X and Y chromosomes may not be in the standard band mapping
        // This test just verifies the method doesn't crash and returns a valid list
        assertTrue(bands != null, "Should return a valid list");
    }

    @Test
    @DisplayName("getAllBandsInArm should handle Y chromosome")
    public void testGetAllBandsInArmYChromosome() {
        List<String> bands = BandRangeCalculator.getAllBandsInArm("yp");

        assertNotNull(bands, "Band list should not be null");
        // X and Y chromosomes may not be in the standard band mapping
        // This test just verifies the method doesn't crash and returns a valid list
        assertTrue(bands != null, "Should return a valid list");
    }

    @Test
    @DisplayName("getAllBandsInArm should return empty list for invalid arm")
    public void testGetAllBandsInArmInvalid() {
        List<String> bands = BandRangeCalculator.getAllBandsInArm("99p");

        assertNotNull(bands, "Band list should not be null");
        assertTrue(bands.isEmpty(), "Invalid arm should return empty list");
    }

    @Test
    @DisplayName("getAllBandsInArm should be case insensitive")
    public void testGetAllBandsInArmCaseInsensitive() {
        List<String> bandsUpper = BandRangeCalculator.getAllBandsInArm("13P");
        List<String> bandsLower = BandRangeCalculator.getAllBandsInArm("13p");

        assertEquals(bandsUpper.size(), bandsLower.size(),
                "Upper and lower case should return same results");
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Band ranges should match actual chromosome band definitions")
    public void testBandRangesMatchDefinitions() {
        // Test that bands returned are actual valid bands in the system
        List<String> bands = BandRangeCalculator.getAllBandsBetween("13q11", "13q14");

        for (String band : bands) {
            Integer index = parseEvent.getChrToIndexMap().get(band);
            assertNotNull(index, "Band " + band + " should exist in chromosome mapping");
        }
    }

    @Test
    @DisplayName("Band order should be sequential (pter to qter)")
    public void testBandOrderSequential() {
        List<String> bands = BandRangeCalculator.getAllBandsBetween("13q11", "13q21");

        // Get indices for all bands
        Integer prevIndex = null;
        for (String band : bands) {
            Integer index = parseEvent.getChrToIndexMap().get(band);
            assertNotNull(index, "Band " + band + " should have index");

            if (prevIndex != null) {
                assertTrue(index >= prevIndex,
                        "Bands should be in sequential order (indices increasing)");
            }
            prevIndex = index;
        }
    }

    @Test
    @DisplayName("Entire chromosome arm should have reasonable number of bands")
    public void testEntireArmBandCount() {
        List<String> chr1p = BandRangeCalculator.getAllBandsInArm("1p");
        List<String> chr1q = BandRangeCalculator.getAllBandsInArm("1q");

        // Chromosome 1 has many bands
        assertTrue(chr1p.size() > 10, "Chr 1p should have > 10 bands");
        assertTrue(chr1q.size() > 20, "Chr 1q should have > 20 bands");

        List<String> chr22q = BandRangeCalculator.getAllBandsInArm("22q");
        // Chromosome 22 has fewer bands
        assertTrue(chr22q.size() > 5, "Chr 22q should have > 5 bands");
    }
}
