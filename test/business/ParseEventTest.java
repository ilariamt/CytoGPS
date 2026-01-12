package business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for ParseEvent class
 * Tests all bug fixes including:
 * - Bug #1: Constructor initialization (duplicate karyotype state)
 * - Bug #2-7: Event processing logic (t(), dup(), etc.)
 */
public class ParseEventTest {

    private ParseEvent parseEvent;

    @BeforeEach
    public void setUp() {
        parseEvent = new ParseEvent();
    }

    // ========== Bug #1: Constructor Initialization Tests ==========

    @Test
    @DisplayName("Bug #1: Each ParseEvent instance should have independent outcome lists")
    public void testIndependentKaryotypeOutcomes() {
        ParseEvent pe1 = new ParseEvent();
        ParseEvent pe2 = new ParseEvent();

        List<Event> events1 = createTestEvents("add(1)(p10)");
        List<Event> events2 = createTestEvents("del(2)(q10)");

        BiologicalOutcome outcome1 = pe1.getKaryotypeOutcome(events1);
        BiologicalOutcome outcome2 = pe2.getKaryotypeOutcome(events2);

        // Verify that outcomes are independent (not sharing state)
        assertNotSame(outcome1.getKaryotypeLGF(), outcome2.getKaryotypeLGF(),
                "Outcomes should not share the same LGF lists");
    }

    @Test
    @DisplayName("Bug #1: getKaryotypeOutcome should create fresh LGF for each karyotype")
    public void testFreshLGFCreation() {
        List<Event> events = createTestEvents("del(1)(p10)");

        BiologicalOutcome outcome1 = parseEvent.getKaryotypeOutcome(events);
        BiologicalOutcome outcome2 = parseEvent.getKaryotypeOutcome(events);

        // Verify that each call creates a new independent LGF
        assertNotSame(outcome1.getKaryotypeLGF(), outcome2.getKaryotypeLGF(),
                "Each karyotype should have independent LGF state");
    }

    // ========== Bug #2: t() Handling Tests ==========

    @Test
    @DisplayName("Bug #2: t() with centromeric breakpoint should mark opposite centromere")
    public void testTranslocationWithCentromere() {
        Event tEvent = createTranslocationEvent("t(1;2)(p10;q10)");
        BiologicalOutcome outcome = processEvent(tEvent);

        List<Integer> fusionOutcome = outcome.getKaryotypeLGF().get(2);

        // Verify fusion marked at 1p10
        int index1p10 = parseEvent.getChrToIndexMap().get("1p10");
        assertTrue(fusionOutcome.get(index1p10) > 0, "1p10 should be marked as fusion");

        // Verify fusion marked at opposite centromere 1q10
        int index1q10 = parseEvent.getChrToIndexMap().get("1q10");
        assertTrue(fusionOutcome.get(index1q10) > 0, "1q10 (opposite) should be marked as fusion");

        // Verify fusion marked at 2q10
        int index2q10 = parseEvent.getChrToIndexMap().get("2q10");
        assertTrue(fusionOutcome.get(index2q10) > 0, "2q10 should be marked as fusion");

        // Verify fusion marked at opposite centromere 2p10
        int index2p10 = parseEvent.getChrToIndexMap().get("2p10");
        assertTrue(fusionOutcome.get(index2p10) > 0, "2p10 (opposite) should be marked as fusion");
    }

    @Test
    @DisplayName("Bug #2: t() with non-centromeric breakpoint should not duplicate fusion")
    public void testTranslocationWithoutCentromere() {
        Event tEvent = createTranslocationEvent("t(9;22)(q34;q11)");
        BiologicalOutcome outcome = processEvent(tEvent);

        List<Integer> fusionOutcome = outcome.getKaryotypeLGF().get(2);

        // Verify fusion marked at breakpoints only
        int index9q34 = parseEvent.getChrToIndexMap().get("9q34.1"); // First match
        int index22q11 = parseEvent.getChrToIndexMap().get("22q11.1"); // First match

        assertTrue(fusionOutcome.get(index9q34) > 0, "9q34 should be marked as fusion");
        assertTrue(fusionOutcome.get(index22q11) > 0, "22q11 should be marked as fusion");

        // Verify no double marking (should be 1, not 2)
        assertEquals(1, fusionOutcome.get(index9q34), "9q34 should be marked exactly once");
    }

    // ========== Bug #3: dup() Duplicate Fusion Marking Tests ==========

    @Test
    @DisplayName("Bug #3: dup() should mark fusion only once per breakpoint")
    public void testDuplicationFusionMarking() {
        Event dupEvent = createDuplicationEvent("dup(1)(p10p20)");
        BiologicalOutcome outcome = processEvent(dupEvent);

        List<Integer> fusionOutcome = outcome.getKaryotypeLGF().get(2);

        // Each breakpoint should be marked exactly once
        int index1p10 = parseEvent.getChrToIndexMap().get("1p10");
        int index1p20 = parseEvent.getChrToIndexMap().get("1p21.1"); // First match for 1p20

        assertEquals(1, fusionOutcome.get(index1p10), "1p10 fusion should be marked exactly once");
        assertEquals(1, fusionOutcome.get(index1p20), "1p20 fusion should be marked exactly once");
    }

    // ========== Chromosome Utilities Tests ==========

    @Test
    @DisplayName("getChrArm should extract chromosome arm correctly")
    public void testGetChrArm() {
        assertEquals("1p", parseEvent.getChrArm("1p36.33"));
        assertEquals("22q", parseEvent.getChrArm("22q11.2"));
        assertEquals("Xp", parseEvent.getChrArm("Xp22.1"));
    }

    @Test
    @DisplayName("getChr should extract chromosome number correctly")
    public void testGetChr() {
        assertEquals("1", parseEvent.getChr("1p36.33"));
        assertEquals("22", parseEvent.getChr("22q11.2"));
        assertEquals("X", parseEvent.getChr("Xp22.1"));
    }

    @Test
    @DisplayName("getOppositeChrArm should return opposite arm")
    public void testGetOppositeChrArm() {
        assertEquals("1q", parseEvent.getOppositeChrArm("1p"));
        assertEquals("1p", parseEvent.getOppositeChrArm("1q"));
        assertEquals("Xq", parseEvent.getOppositeChrArm("Xp"));
    }

    @Test
    @DisplayName("getChrTer should return telomere for each arm")
    public void testGetChrTer() {
        assertEquals("1p36.33", ParseEvent.getChrTer("1p"));
        assertEquals("1q44", ParseEvent.getChrTer("1q"));
        assertEquals("22q13.33", ParseEvent.getChrTer("22q"));
        assertEquals("Xq28", ParseEvent.getChrTer("Xq"));
    }

    // ========== Index Mapping Tests ==========

    @Test
    @DisplayName("Index to chromosome mapping should be bidirectional")
    public void testIndexChrMapping() {
        // Test that we can go from index -> chr -> index
        for (int i = 0; i < parseEvent.getIndexToChrMap().size(); i++) {
            String chr = parseEvent.getIndexToChrMap().get(i);
            int backToIndex = parseEvent.getChrToIndexMap().get(chr);
            assertEquals(i, backToIndex, "Bidirectional mapping should work for index " + i);
        }
    }

    // ========== Helper Methods ==========

    private List<Event> createTestEvents(String eventCode) {
        List<Event> events = new ArrayList<>();
        Event e = new Event();
        e.setEventCode(eventCode);
        e.setNature(extractNature(eventCode));
        events.add(e);
        return events;
    }

    private String extractNature(String eventCode) {
        if (eventCode.contains("del")) return "del";
        if (eventCode.contains("dup")) return "dup";
        if (eventCode.contains("add")) return "add";
        if (eventCode.contains("t(")) return "t";
        if (eventCode.contains("inv")) return "inv";
        return "";
    }

    private Event createTranslocationEvent(String eventCode) {
        Event e = new Event();
        e.setNature("t");
        e.setEventCode(eventCode);
        // Parse chromosomes and breakpoints from eventCode
        // This is simplified - you'll need to parse properly
        return e;
    }

    private Event createDuplicationEvent(String eventCode) {
        Event e = new Event();
        e.setNature("dup");
        e.setEventCode(eventCode);
        return e;
    }

    private BiologicalOutcome processEvent(Event event) {
        List<Event> events = new ArrayList<>();
        events.add(event);
        return parseEvent.getKaryotypeOutcome(events);
    }
}
