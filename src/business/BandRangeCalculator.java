package business;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for calculating chromosome band ranges
 * Uses the ISCN 2016 band definitions from BiologicalOutcome
 *
 * @author ilariamt
 * Date: January 02, 2026
 * Updated: January 27, 2026 - Fixed reversed telomere direction handling
 */
public class BandRangeCalculator {

    private static ParseEvent parseEvent = new ParseEvent();

    /**
     * Get all bands between two breakpoints (inclusive)
     * Example: getAllBandsBetween("15q10", "15q21")
     * Returns: ["15q10", "15q11.1", "15q11.2", "15q12", ..., "15q21.1", "15q21.2", "15q21.3"]
     *
     * IMPORTANT: This method now handles REVERSED segments like:
     * - "16qter->16q22" (from qter backwards to q22)
     * - "17p13->17pter" (from p13 to pter)
     *
     * IMPORTANT: This method now handles CROSS-ARM segments like:
     * - "16p11->16q22" (from p11 through centromere to q22)
     * When crossing the centromere, the centromeric band (p10) must be included.
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

        // Handle special cases with telomeres
        if (start.contains("pter") || start.contains("qter") ||
            end.contains("pter") || end.contains("qter")) {
            return getBandsWithTelomeres(start, end, indexToChrMap, chrToIndexMap);
        }

        // Check for cross-arm segment (start on p arm, end on q arm or vice versa)
        String startChr = extractChr(start);
        String endChr = extractChr(end);
        String startArm = extractArm(start);
        String endArm = extractArm(end);

        // Handle cross-arm segments specially
        if (startChr.equals(endChr) && !startArm.isEmpty() && !endArm.isEmpty() && !startArm.equals(endArm)) {
            return getBandsCrossArm(start, end, startChr, startArm, endArm, indexToChrMap, chrToIndexMap);
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

        // Ensure startIndex <= endIndex (handle reversed ranges)
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
     * Handle cross-arm segments that traverse the centromere
     * Example: "16p11->16q22" goes from p11 through p10 (centromere) to q10, then to q22
     *
     * The key insight is that when crossing arms, we need to include the centromeric
     * band (p10) which might otherwise be missed because the indexing goes:
     * ... p10 (low), p11.1 (higher), p11.2, ... p13.3, q10, q11.1, ... q22.1 ...
     *
     * So going from p11 to q22 would normally skip p10 unless we handle it specially.
     */
    private static List<String> getBandsCrossArm(String start, String end, String chr,
                                                   String startArm, String endArm,
                                                   java.util.Map<Integer, String> indexToChrMap,
                                                   java.util.Map<String, Integer> chrToIndexMap) {
        List<String> bands = new ArrayList<>();

        // Determine which arm is "first" (p arm) and which is "second" (q arm)
        // In the index ordering, p arm bands come before q arm bands for each chromosome
        boolean startOnP = startArm.equals("p");

        // Get the centromere indices
        String centromereP = chr + "p10";
        String centromereQ = chr + "q10";
        Integer centromerePIndex = chrToIndexMap.get(centromereP.toLowerCase());
        Integer centromereQIndex = chrToIndexMap.get(centromereQ.toLowerCase());

        if (centromerePIndex == null || centromereQIndex == null) {
            // Can't find centromere bands - fall back to simple range
            return bands;
        }

        // Get start and end indices
        Integer startIndex = chrToIndexMap.get(start.toLowerCase());
        if (startIndex == null) {
            startIndex = findClosestBandIndex(start, chrToIndexMap);
        }
        Integer endIndex = chrToIndexMap.get(end.toLowerCase());
        if (endIndex == null) {
            endIndex = findClosestBandIndex(end, chrToIndexMap);
        }

        // Also find the outermost indices for imprecise bands
        Integer startOutermost = findOutermostBandIndex(start, chrToIndexMap);
        Integer endOutermost = findOutermostBandIndex(end, chrToIndexMap);

        if (startIndex == null || endIndex == null) {
            return bands;
        }

        if (startOnP) {
            // Start is on p arm, end is on q arm
            // Path: start -> p10 -> q10 -> end
            // Include: p arm bands from p10 to start, then q arm bands from q10 to end

            // Step 1: Add all p arm bands from p10 (centromere) to start (and all its subbands)
            int pStart = centromerePIndex;
            int pEnd = (startOutermost != null) ? startOutermost : startIndex;
            for (int i = pStart; i <= pEnd; i++) {
                String band = indexToChrMap.get(i);
                if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "p")) {
                    bands.add(band);
                }
            }

            // Step 2: Add all q arm bands from q10 (centromere) to end (and all its subbands)
            int qStart = centromereQIndex;
            int qEnd = (endOutermost != null) ? endOutermost : endIndex;
            for (int i = qStart; i <= qEnd; i++) {
                String band = indexToChrMap.get(i);
                if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "q")) {
                    bands.add(band);
                }
            }
        } else {
            // Start is on q arm, end is on p arm
            // Path: start -> q10 -> p10 -> end
            // Include: q arm bands from q10 to start, then p arm bands from p10 to end

            // Step 1: Add all q arm bands from q10 (centromere) to start (and all its subbands)
            int qStart = centromereQIndex;
            int qEnd = (startOutermost != null) ? startOutermost : startIndex;
            for (int i = qStart; i <= qEnd; i++) {
                String band = indexToChrMap.get(i);
                if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "q")) {
                    bands.add(band);
                }
            }

            // Step 2: Add all p arm bands from p10 (centromere) to end (and all its subbands)
            int pStart = centromerePIndex;
            int pEnd = (endOutermost != null) ? endOutermost : endIndex;
            for (int i = pStart; i <= pEnd; i++) {
                String band = indexToChrMap.get(i);
                if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "p")) {
                    bands.add(band);
                }
            }
        }

        return bands;
    }

    /**
     * Handle bands with telomere markers (pter, qter)
     * 
     * UPDATED: Now handles all 4 cases:
     * 1. pter->X (from pter to some point on p arm)
     * 2. X->qter (from some point to qter)
     * 3. qter->X (REVERSED: from qter backwards) - e.g., 16qter->16q22
     * 4. X->pter (REVERSED: from some point to pter) - e.g., 17p13->17pter
     */
    private static List<String> getBandsWithTelomeres(String start, String end,
                                                       java.util.Map<Integer, String> indexToChrMap,
                                                       java.util.Map<String, Integer> chrToIndexMap) {
        List<String> bands = new ArrayList<>();

        // Case 1: start contains "pter" -> from pter (distal) to end point
        // Example: 6pter->6p21 means bands from 6p25.3 (most distal) down to 6p21 (endpoint)
        // P arm bands are indexed from centromere (p10=low index) to telomere (pter=high index)
        if (start.contains("pter")) {
            String chr = extractChr(start);
            
            // Find the endpoint index (e.g., 6p21)
            Integer endIndex = chrToIndexMap.get(end.toLowerCase());
            if (endIndex == null) {
                endIndex = findClosestBandIndex(end, chrToIndexMap);
            }
            
            // For imprecise bands like "6p21", find the OUTERMOST subband to include all of them
            Integer outermostEndIndex = findOutermostBandIndex(end, chrToIndexMap);
            if (outermostEndIndex == null) {
                outermostEndIndex = endIndex;
            }
            
            if (endIndex != null) {
                // Find the highest (most distal) p arm band for this chromosome (pter)
                Integer pterIndex = null;
                for (int i = 0; i < indexToChrMap.size(); i++) {
                    String band = indexToChrMap.get(i);
                    if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "p")) {
                        if (pterIndex == null || i > pterIndex) {
                            pterIndex = i;
                        }
                    }
                }
                
                if (pterIndex != null) {
                    // Include all bands from endIndex (innermost of endpoint region) to pterIndex (pter)
                    for (int i = endIndex; i <= pterIndex; i++) {
                        String band = indexToChrMap.get(i);
                        if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "p")) {
                            bands.add(band);
                        }
                    }
                }
            }
            return bands;
        }

        // Case 2: end contains "qter" -> from start point to qter
        // IMPORTANT: Handle cross-arm segments like "6p21->6qter" which span from p arm across centromere to q arm
        if (end.contains("qter")) {
            String chr = extractChr(start);
            Integer startIndex = chrToIndexMap.get(start.toLowerCase());
            
            if (startIndex == null) {
                startIndex = findClosestBandIndex(start, chrToIndexMap);
            }

            if (startIndex != null) {
                String startBand = indexToChrMap.get(startIndex);
                boolean startsOnPArm = startBand != null && startBand.toLowerCase().contains("p");
                
                if (startsOnPArm) {
                    // Cross-arm segment: need p arm bands from start to centromere, then all q arm bands
                    // Step 1: Add p arm bands from start down to centromere (p10)
                    // p arm bands are ordered: p10, p11, p12, ... (lower index = closer to centromere)
                    // IMPORTANT: For imprecise bands like "6p21", we need to include ALL subbands
                    // So we find the OUTERMOST subband (e.g., 6p21.33) to include the full range
                    Integer outermostStartIndex = findOutermostBandIndex(start, chrToIndexMap);
                    if (outermostStartIndex == null) {
                        outermostStartIndex = startIndex;
                    }
                    
                    String centromereP = chr + "p10";
                    Integer centromerePIndex = chrToIndexMap.get(centromereP.toLowerCase());
                    if (centromerePIndex != null) {
                        for (int i = centromerePIndex; i <= outermostStartIndex; i++) {
                            String band = indexToChrMap.get(i);
                            if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "p")) {
                                bands.add(band);
                            }
                        }
                    }
                    
                    // Step 2: Add all q arm bands from centromere (q10) to qter
                    String centromereQ = chr + "q10";
                    Integer centromereQIndex = chrToIndexMap.get(centromereQ.toLowerCase());
                    if (centromereQIndex != null) {
                        for (int i = centromereQIndex; i < indexToChrMap.size(); i++) {
                            String band = indexToChrMap.get(i);
                            if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "q")) {
                                bands.add(band);
                            } else if (band != null && !band.toLowerCase().startsWith(chr.toLowerCase())) {
                                break;
                            }
                        }
                    }
                } else {
                    // Start is on q arm - just add all bands from start to qter
                    for (int i = startIndex; i < indexToChrMap.size(); i++) {
                        String band = indexToChrMap.get(i);
                        if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "q")) {
                            bands.add(band);
                        } else if (band != null && !band.toLowerCase().startsWith(chr.toLowerCase())) {
                            break;
                        }
                    }
                }
            }
            return bands;
        }

        // Case 3: start contains "qter" -> REVERSED: from qter backwards to end point
        // Example: "16qter->16q22" means bands from 16q22 to 16qter (all of them)
        if (start.contains("qter")) {
            String chr = extractChr(start);
            Integer endIndex = chrToIndexMap.get(end.toLowerCase());
            
            if (endIndex == null) {
                endIndex = findClosestBandIndex(end, chrToIndexMap);
            }

            if (endIndex != null) {
                // Find all bands from endIndex to the last q arm band (qter)
                for (int i = endIndex; i < indexToChrMap.size(); i++) {
                    String band = indexToChrMap.get(i);
                    if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "q")) {
                        bands.add(band);
                    } else if (band != null && !band.toLowerCase().startsWith(chr.toLowerCase())) {
                        // We've moved to a different chromosome
                        break;
                    }
                }
            }
            return bands;
        }

        // Case 4: end contains "pter" -> REVERSED: from start point to pter
        // Example: "17p13->17pter" means bands from p13 to pter (all of them up to pter)
        if (end.contains("pter")) {
            String chr = extractChr(start);
            Integer startIndex = chrToIndexMap.get(start.toLowerCase());
            
            if (startIndex == null) {
                startIndex = findClosestBandIndex(start, chrToIndexMap);
            }

            if (startIndex != null) {
                // We need all bands from startIndex to the last p arm band
                // Since p arm bands are ordered from centromere (p10) outward (p11, p12, ..., pter)
                // we need to find all p arm bands >= startIndex
                for (int i = startIndex; i < indexToChrMap.size(); i++) {
                    String band = indexToChrMap.get(i);
                    if (band != null && band.toLowerCase().startsWith(chr.toLowerCase() + "p")) {
                        bands.add(band);
                    } else if (band != null && !band.toLowerCase().startsWith(chr.toLowerCase() + "p")) {
                        // We've moved past p arm
                        break;
                    }
                }
            }
            return bands;
        }

        return bands;
    }

    /**
     * Find the closest band index when exact match not found
     * Handles cases like "15q21" when only "15q21.1", "15q21.2", etc. exist
     * Returns the INNERMOST (lowest index) subband - closest to centromere
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

        // Search for any band that starts with this pattern - find the LOWEST index
        Integer lowestIndex = null;
        for (String key : chrToIndexMap.keySet()) {
            if (key.startsWith(band.toLowerCase())) {
                Integer keyIndex = chrToIndexMap.get(key);
                if (lowestIndex == null || keyIndex < lowestIndex) {
                    lowestIndex = keyIndex;
                }
            }
        }
        return lowestIndex;
    }

    /**
     * Find the OUTERMOST (highest index) subband for an imprecise band
     * For example, "6p21" -> returns index of 6p21.33 (the most distal subband)
     * This is needed when we want to include ALL subbands of a region
     */
    private static Integer findOutermostBandIndex(String band, java.util.Map<String, Integer> chrToIndexMap) {
        // First check if this is an exact match
        Integer exactIndex = chrToIndexMap.get(band.toLowerCase());
        if (exactIndex != null) {
            return exactIndex;
        }

        // Search for all bands that start with this pattern and find the HIGHEST index
        Integer highestIndex = null;
        for (String key : chrToIndexMap.keySet()) {
            if (key.startsWith(band.toLowerCase())) {
                Integer keyIndex = chrToIndexMap.get(key);
                if (highestIndex == null || keyIndex > highestIndex) {
                    highestIndex = keyIndex;
                }
            }
        }
        return highestIndex;
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
