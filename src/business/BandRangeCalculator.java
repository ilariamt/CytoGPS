package business;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for calculating chromosome band ranges
 * Uses the ISCN 2016 band definitions from BiologicalOutcome
 *
 * @author ilariamt
 * Date: January 02, 2026
 */
public class BandRangeCalculator {

    private static ParseEvent parseEvent = new ParseEvent();

    /**
     * Get all bands between two breakpoints (inclusive)
     * Example: getAllBandsBetween("15q10", "15q21")
     * Returns: ["15q10", "15q11.1", "15q11.2", "15q12", ..., "15q21.1", "15q21.2", "15q21.3"]
     *
     * @param start Starting breakpoint (e.g., "15q10")
     * @param end Ending breakpoint (e.g., "15q21")
     * @return List of all bands in the range
     */
    public static List<String> getAllBandsBetween(String start, String end) {
        List<String> bands = new ArrayList<>();

        // Get the index-to-chr mapping
        java.util.Map<Integer, String> indexToChrMap = parseEvent.getIndexToChrMap();
        java.util.Map<String, Integer> chrToIndexMap = parseEvent.getChrToIndexMap();

        // Handle special cases
        if (start.contains("pter") || start.contains("qter") ||
            end.contains("pter") || end.contains("qter")) {
            return getBandsWithTelomeres(start, end, indexToChrMap, chrToIndexMap);
        }

        // Get indices for start and end
        Integer startIndex = chrToIndexMap.get(start.toLowerCase());
        Integer endIndex = chrToIndexMap.get(end.toLowerCase());

        if (startIndex == null || endIndex == null) {
            // Try without subband precision
            startIndex = findClosestBandIndex(start, chrToIndexMap);
            endIndex = findClosestBandIndex(end, chrToIndexMap);
        }

        if (startIndex == null || endIndex == null) {
            return bands; // Return empty if we can't find the bands
        }

        // Ensure startIndex <= endIndex
        if (startIndex > endIndex) {
            int temp = startIndex;
            startIndex = endIndex;
            endIndex = temp;
        }

        // Collect all bands in the range
        for (int i = startIndex; i <= endIndex; i++) {
            String band = indexToChrMap.get(i);
            if (band != null) {
                bands.add(band);
            }
        }

        return bands;
    }

    /**
     * Handle bands with telomere markers (pter, qter)
     */
    private static List<String> getBandsWithTelomeres(String start, String end,
                                                       java.util.Map<Integer, String> indexToChrMap,
                                                       java.util.Map<String, Integer> chrToIndexMap) {
        List<String> bands = new ArrayList<>();

        // Extract chromosome and arm
        String chr = extractChr(start.contains("pter") || start.contains("qter") ? start : end);
        String arm = extractArm(start.contains("pter") || start.contains("qter") ? start : end);

        // Get all bands for this chromosome arm
        if (start.contains("pter")) {
            // From pter to end point
            String armStart = chr + arm + "10"; // Centromere
            Integer endIndex = chrToIndexMap.get(end.toLowerCase());
            if (endIndex == null) {
                endIndex = findClosestBandIndex(end, chrToIndexMap);
            }
            Integer startIndex = chrToIndexMap.get(armStart.toLowerCase());

            if (startIndex != null && endIndex != null) {
                for (int i = startIndex; i <= endIndex; i++) {
                    String band = indexToChrMap.get(i);
                    if (band != null && band.startsWith(chr + arm)) {
                        bands.add(band);
                    }
                }
            }
        } else if (end.contains("qter")) {
            // From start point to qter
            Integer startIndex = chrToIndexMap.get(start.toLowerCase());
            if (startIndex == null) {
                startIndex = findClosestBandIndex(start, chrToIndexMap);
            }

            if (startIndex != null) {
                // Find the last band for this chromosome's q arm
                for (int i = startIndex; i < indexToChrMap.size(); i++) {
                    String band = indexToChrMap.get(i);
                    if (band != null && band.startsWith(chr + "q")) {
                        bands.add(band);
                    } else if (band != null && !band.startsWith(chr)) {
                        // We've moved to a different chromosome
                        break;
                    }
                }
            }
        }

        return bands;
    }

    /**
     * Find the closest band index when exact match not found
     * Handles cases like "15q21" when only "15q21.1", "15q21.2", etc. exist
     */
    private static Integer findClosestBandIndex(String band, java.util.Map<String, Integer> chrToIndexMap) {
        // Try with .1 suffix (most common)
        Integer index = chrToIndexMap.get(band.toLowerCase() + ".1");
        if (index != null) {
            return index;
        }

        // Try without any decimal
        String bandBase = band.replaceAll("\\..*", "");
        index = chrToIndexMap.get(bandBase.toLowerCase());
        if (index != null) {
            return index;
        }

        // Search for any band that starts with this pattern
        for (String key : chrToIndexMap.keySet()) {
            if (key.startsWith(band.toLowerCase())) {
                return chrToIndexMap.get(key);
            }
        }

        return null;
    }

    /**
     * Extract chromosome number from a breakpoint
     * Example: "15q21" -> "15", "Xp11" -> "x"
     */
    private static String extractChr(String breakpoint) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^(\\d+|[XYxy])");
        java.util.regex.Matcher matcher = pattern.matcher(breakpoint);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }
        return "";
    }

    /**
     * Extract arm (p or q) from a breakpoint
     * Example: "15q21" -> "q", "Xp11" -> "p"
     */
    private static String extractArm(String breakpoint) {
        if (breakpoint.contains("p")) {
            return "p";
        } else if (breakpoint.contains("q")) {
            return "q";
        }
        return "";
    }

    /**
     * Get all bands in a chromosome arm
     * Example: getAllBandsInArm("13p")
     * Returns: ["13p10", "13p11.1", "13p11.2", ..., "13p13"]
     */
    public static List<String> getAllBandsInArm(String chrArm) {
        List<String> bands = new ArrayList<>();
        java.util.Map<Integer, String> indexToChrMap = parseEvent.getIndexToChrMap();

        String chrArmLower = chrArm.toLowerCase();

        for (int i = 0; i < indexToChrMap.size(); i++) {
            String band = indexToChrMap.get(i);
            if (band != null && band.startsWith(chrArmLower)) {
                bands.add(band);
            }
        }

        return bands;
    }
}