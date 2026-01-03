package business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for detailed derivative chromosome formulas
 * Format: der(chr)(segment1::segment2::segment3)
 * Example: der(13)(13pter->13q10::15q10->15q21::13q14->13qter)
 *
 * Supported edge cases:
 * - Standard derivatives: der(13)(...)
 * - Dicentric chromosomes: der(13;15)(...)
 * - Ring chromosomes: der(7)(::7q11->7q31::)
 * - HSR regions: der(8)(...::hsr::...)
 * - Multiple fragments from same chromosome
 * - Telomere markers: pter, qter
 * - Sub-band precision: 15q21.1, 15q21.2, etc.
 *
 * @author ilariamt
 * Date: January 02, 2026
 */
public class DetailedFormulaParser {

    private ParseEvent parseEvent = new ParseEvent();

    /**
     * Check if input contains detailed formula syntax
     */
    public static boolean isDetailedFormula(String input) {
        // Pattern: der(X)(...pter->...::...->...qter...)
        // or der(X)(...->...::...->...)
        Pattern pattern = Pattern.compile("der\\([^)]+\\)\\([^)]*(?:->|::)[^)]*\\)");
        return pattern.matcher(input).find();
    }

    /**
     * Parse a detailed formula and extract LGF
     * Input: "46,XX,der(13)(13pter->13q10::15q10->15q21::13q14->13qter)"
     * Output: BiologicalOutcome with LGF data
     */
    public BiologicalOutcome parseDetailedFormula(String input) {
        // Initialize LGF
        List<String> uncertainEventsList = new ArrayList<>();
        List<String> derDetailedSystem = new ArrayList<>();

        List<List<Integer>> initKaryotypeLGF = new ArrayList<>();
        initKaryotypeLGF.add(new ArrayList<>(Collections.nCopies(parseEvent.getIndexToChrMap().size(), 0)));  // loss
        initKaryotypeLGF.add(new ArrayList<>(Collections.nCopies(parseEvent.getIndexToChrMap().size(), 0)));  // gain
        initKaryotypeLGF.add(new ArrayList<>(Collections.nCopies(parseEvent.getIndexToChrMap().size(), 0)));  // fusion

        BiologicalOutcome b = new BiologicalOutcome(initKaryotypeLGF, uncertainEventsList, derDetailedSystem);

        // Extract detailed derivative formulas
        Pattern derPattern = Pattern.compile("der\\(([^)]+)\\)\\(([^)]+)\\)");
        Matcher derMatcher = derPattern.matcher(input);

        while (derMatcher.find()) {
            String baseChrSpec = derMatcher.group(1);
            String detailedFormula = derMatcher.group(2);

            // Add to detailed system
            derDetailedSystem.add("der(" + baseChrSpec + ")(" + detailedFormula + ")");

            // Parse base chromosome(s)
            // Can be single chr: der(13) or dicentric: der(13;15)
            String[] baseChrs = baseChrSpec.split(";");

            if (baseChrs.length == 1) {
                // Standard derivative with single base chromosome
                parseDetailedSegments(detailedFormula, baseChrs[0].trim(), b.getKaryotypeLGF());
            } else {
                // Dicentric: multiple base chromosomes
                // Each base chromosome gets loss detection
                parseDetailedSegmentsDicentric(detailedFormula, baseChrs, b.getKaryotypeLGF());
            }
        }

        return b;
    }

    /**
     * Parse detailed segments and compute LGF
     * Format: 13pter->13q10::15q10->15q21::13q14->13qter
     *
     * Handles edge cases:
     * - Ring chromosomes: ::segment::
     * - HSR regions: ...::hsr::...
     * - Isochromosomes: i(13)(q10) in detailed formulas
     * - Multiple fragments from same chromosome
     */
    private void parseDetailedSegments(String formula, String baseChr, List<List<Integer>> karyotypeLGF) {
        List<Integer> karyotypeLossOutcome = karyotypeLGF.get(0);
        List<Integer> karyotypeGainOutcome = karyotypeLGF.get(1);
        List<Integer> karyotypeFusionOutcome = karyotypeLGF.get(2);

        // Check for ring chromosome (starts and ends with ::)
        boolean isRing = formula.startsWith("::") && formula.endsWith("::");

        // Remove leading/trailing :: for ring chromosomes
        String cleanedFormula = formula;
        if (isRing) {
            cleanedFormula = formula.substring(2, formula.length() - 2);
        }

        // Split by fusion points (::)
        String[] segments = cleanedFormula.split("::");

        for (String segment : segments) {
            segment = segment.trim();

            // Skip empty segments
            if (segment.isEmpty()) {
                continue;
            }

            // Handle HSR (homogeneously staining region)
            if (segment.equals("hsr") || segment.equalsIgnoreCase("hsr")) {
                // HSR is a special marker, mark as fusion but don't process as gain/loss
                // It represents amplified material, but we don't know the exact bands
                continue;
            }

            // Parse segment: "13pter->13q10" or "15q10->15q21"
            String[] parts = segment.split("->");

            if (parts.length == 2) {
                String startPoint = parts[0].trim();
                String endPoint = parts[1].trim();

                // Determine if this is a gain or present segment
                String segmentChr = extractChr(startPoint);

                if (segmentChr.equals(baseChr)) {
                    // Segment from base chromosome - normal (no gain)
                    // But mark fusion points
                    markFusionPoint(karyotypeFusionOutcome, startPoint);
                    markFusionPoint(karyotypeFusionOutcome, endPoint);
                } else {
                    // Segment from different chromosome - this is a GAIN
                    recordSegmentGain(karyotypeGainOutcome, karyotypeFusionOutcome, startPoint, endPoint);
                }
            } else if (parts.length == 1) {
                // Single point (standalone breakpoint)
                String point = parts[0].trim();
                if (!point.isEmpty() && !point.equals("hsr")) {
                    markFusionPoint(karyotypeFusionOutcome, point);
                }
            }
        }

        // Detect losses: any part of base chromosome NOT in the formula
        // For ring chromosomes, losses are calculated the same way
        detectLosses(cleanedFormula, baseChr, karyotypeLossOutcome, isRing);
    }

    /**
     * Parse detailed segments for dicentric chromosomes
     * Format: der(13;15)(13pter->13q10::15q10->15q21)
     *
     * Dicentric handling:
     * - Multiple base chromosomes (e.g., 13 and 15 in der(13;15))
     * - Losses are reported on ALL base chromosomes
     * - Material from non-base chromosomes is still gained
     */
    private void parseDetailedSegmentsDicentric(String formula, String[] baseChrs, List<List<Integer>> karyotypeLGF) {
        List<Integer> karyotypeLossOutcome = karyotypeLGF.get(0);
        List<Integer> karyotypeGainOutcome = karyotypeLGF.get(1);
        List<Integer> karyotypeFusionOutcome = karyotypeLGF.get(2);

        // Check for ring chromosome
        boolean isRing = formula.startsWith("::") && formula.endsWith("::");
        String cleanedFormula = formula;
        if (isRing) {
            cleanedFormula = formula.substring(2, formula.length() - 2);
        }

        // Trim all base chromosomes
        for (int i = 0; i < baseChrs.length; i++) {
            baseChrs[i] = baseChrs[i].trim();
        }

        // Split by fusion points (::)
        String[] segments = cleanedFormula.split("::");

        for (String segment : segments) {
            segment = segment.trim();

            // Skip empty segments or HSR
            if (segment.isEmpty() || segment.equalsIgnoreCase("hsr")) {
                continue;
            }

            // Parse segment
            String[] parts = segment.split("->");

            if (parts.length == 2) {
                String startPoint = parts[0].trim();
                String endPoint = parts[1].trim();
                String segmentChr = extractChr(startPoint);

                // Check if this segment is from one of the base chromosomes
                boolean isFromBaseChr = false;
                for (String baseChr : baseChrs) {
                    if (segmentChr.equals(baseChr)) {
                        isFromBaseChr = true;
                        break;
                    }
                }

                if (isFromBaseChr) {
                    // Segment from one of the base chromosomes - mark fusion only
                    markFusionPoint(karyotypeFusionOutcome, startPoint);
                    markFusionPoint(karyotypeFusionOutcome, endPoint);
                } else {
                    // Segment from a different chromosome - this is a GAIN
                    recordSegmentGain(karyotypeGainOutcome, karyotypeFusionOutcome, startPoint, endPoint);
                }
            } else if (parts.length == 1) {
                String point = parts[0].trim();
                if (!point.isEmpty() && !point.equals("hsr")) {
                    markFusionPoint(karyotypeFusionOutcome, point);
                }
            }
        }

        // Detect losses for EACH base chromosome
        for (String baseChr : baseChrs) {
            detectLosses(cleanedFormula, baseChr, karyotypeLossOutcome, isRing);
        }
    }

    /**
     * Extract chromosome number from a breakpoint
     * Example: "13pter" -> "13", "15q21" -> "15"
     */
    private String extractChr(String breakpoint) {
        Pattern pattern = Pattern.compile("^(\\d+|[XY])");
        Matcher matcher = pattern.matcher(breakpoint);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Mark a fusion point in the fusion outcome vector
     */
    private void markFusionPoint(List<Integer> karyotypeFusionOutcome, String breakpoint) {
        // Skip telomeres and special markers
        if (breakpoint.contains("pter") || breakpoint.contains("qter") ||
            breakpoint.equals("hsr") || breakpoint.isEmpty()) {
            return;
        }

        // Breakpoint is already in correct format (e.g., "15q21"), just lowercase it
        String band = breakpoint.toLowerCase();

        // Use parseEvent's mapping to find the index
        Integer index = parseEvent.getChrToIndexMap().get(band);
        if (index != null) {
            karyotypeFusionOutcome.set(index, karyotypeFusionOutcome.get(index) + 1);
        }
    }

    /**
     * Record gain for a segment from a different chromosome
     *
     * Example: recordSegmentGain(..., "15q10", "15q21")
     * Will mark ALL bands from 15q10 to 15q21 as gained
     */
    private void recordSegmentGain(List<Integer> karyotypeGainOutcome, List<Integer> karyotypeFusionOutcome,
                                    String startPoint, String endPoint) {
        // Mark fusion at both ends
        markFusionPoint(karyotypeFusionOutcome, startPoint);
        markFusionPoint(karyotypeFusionOutcome, endPoint);

        // Get all bands between the start and end points
        List<String> bandsInSegment = BandRangeCalculator.getAllBandsBetween(startPoint, endPoint);

        // Mark each band as gained
        for (String band : bandsInSegment) {
            Integer index = parseEvent.getChrToIndexMap().get(band.toLowerCase());
            if (index != null) {
                karyotypeGainOutcome.set(index, karyotypeGainOutcome.get(index) + 1);
            }
        }
    }

    /**
     * Detect which parts of the base chromosome are lost (not in the formula)
     *
     * CRITICAL RULE: Only the chromosome in der(X) gets losses reported!
     * - der(13) → only report losses on chr 13
     * - der(13;15) → report losses on both chr 13 AND chr 15 (dicentric)
     *
     * Algorithm:
     * 1. Extract all segments from the base chromosome
     * 2. Sort segments by position (pter → qter)
     * 3. Find GAPS between consecutive segments
     * 4. Report bands in gaps as losses
     *
     * Example: der(13)(13pter->13q10::15q10->15q21::13q14->13qter)
     * - Segments from chr 13: [13pter->13q10, 13q14->13qter]
     * - Gap: 13q10 to 13q14
     * - Loss: all bands between 13q10 and 13q14
     *
     * Ring chromosome handling:
     * - For rings (::segment::), losses are still calculated normally
     * - The ring structure doesn't change what material is missing
     */
    private void detectLosses(String formula, String baseChr, List<Integer> karyotypeLossOutcome, boolean isRing) {
        // Step 1: Extract all segments from baseChr
        List<Segment> baseSegments = new ArrayList<>();

        String[] segments = formula.split("::");
        for (String segment : segments) {
            segment = segment.trim();

            // Skip ring chromosome markers or empty segments
            if (segment.isEmpty() || segment.equals("hsr")) {
                continue;
            }

            // Parse segment: "13pter->13q10" or "13q14->13qter"
            String[] parts = segment.split("->");
            if (parts.length == 2) {
                String startPoint = parts[0].trim();
                String endPoint = parts[1].trim();
                String segmentChr = extractChr(startPoint);

                // Only collect segments from the base chromosome
                if (segmentChr.equals(baseChr)) {
                    baseSegments.add(new Segment(startPoint, endPoint));
                }
            }
        }

        // If no segments from base chromosome, the entire chromosome is lost
        if (baseSegments.isEmpty()) {
            markEntireChromosomeLoss(baseChr, karyotypeLossOutcome);
            return;
        }

        // Step 2: Sort segments by chromosomal position
        baseSegments.sort((s1, s2) -> compareBreakpoints(s1.start, s2.start));

        // Step 3 & 4: Find gaps and mark losses

        // Check if p arm start is missing (gap before first segment)
        Segment firstSegment = baseSegments.get(0);
        if (!firstSegment.start.contains("pter")) {
            // There's a gap from pter to the start of first segment
            String pArmStart = baseChr + "pter";
            List<String> lostBands = BandRangeCalculator.getAllBandsBetween(pArmStart, firstSegment.start);
            markBandsAsLost(lostBands, karyotypeLossOutcome);
        }

        // Check gaps between consecutive segments
        for (int i = 0; i < baseSegments.size() - 1; i++) {
            Segment current = baseSegments.get(i);
            Segment next = baseSegments.get(i + 1);

            // If current.end != next.start, there's a gap
            if (!current.end.equals(next.start)) {
                List<String> lostBands = BandRangeCalculator.getAllBandsBetween(current.end, next.start);
                markBandsAsLost(lostBands, karyotypeLossOutcome);
            }
        }

        // Check if q arm end is missing (gap after last segment)
        Segment lastSegment = baseSegments.get(baseSegments.size() - 1);
        if (!lastSegment.end.contains("qter")) {
            // There's a gap from end of last segment to qter
            String qArmEnd = baseChr + "qter";
            List<String> lostBands = BandRangeCalculator.getAllBandsBetween(lastSegment.end, qArmEnd);
            markBandsAsLost(lostBands, karyotypeLossOutcome);
        }
    }

    /**
     * Mark all bands in a list as lost
     */
    private void markBandsAsLost(List<String> bands, List<Integer> karyotypeLossOutcome) {
        for (String band : bands) {
            Integer index = parseEvent.getChrToIndexMap().get(band.toLowerCase());
            if (index != null) {
                karyotypeLossOutcome.set(index, karyotypeLossOutcome.get(index) + 1);
            }
        }
    }

    /**
     * Mark entire chromosome as lost (used when no segments from base chr in formula)
     */
    private void markEntireChromosomeLoss(String chr, List<Integer> karyotypeLossOutcome) {
        // Get all bands for both p and q arms
        List<String> pBands = BandRangeCalculator.getAllBandsInArm(chr + "p");
        List<String> qBands = BandRangeCalculator.getAllBandsInArm(chr + "q");

        markBandsAsLost(pBands, karyotypeLossOutcome);
        markBandsAsLost(qBands, karyotypeLossOutcome);
    }

    /**
     * Compare two breakpoints to determine their order on the chromosome
     * Returns negative if bp1 comes before bp2, positive if after, 0 if equal
     */
    private int compareBreakpoints(String bp1, String bp2) {
        // Handle telomeres first
        if (bp1.contains("pter")) return -1;
        if (bp2.contains("pter")) return 1;
        if (bp1.contains("qter")) return 1;
        if (bp2.contains("qter")) return -1;

        // Get indices from the mapping
        Integer index1 = parseEvent.getChrToIndexMap().get(bp1.toLowerCase());
        Integer index2 = parseEvent.getChrToIndexMap().get(bp2.toLowerCase());

        if (index1 != null && index2 != null) {
            return index1.compareTo(index2);
        }

        // Fallback: compare as strings
        return bp1.compareTo(bp2);
    }

    /**
     * Inner class to represent a chromosome segment
     */
    private static class Segment {
        String start;
        String end;

        Segment(String start, String end) {
            this.start = start;
            this.end = end;
        }
    }
}
