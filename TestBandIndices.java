import business.ParseEvent;
import business.BandRangeCalculator;
import java.util.*;

public class TestBandIndices {
    public static void main(String[] args) {
        ParseEvent pe = new ParseEvent();
        Map<String, Integer> chrToIndex = pe.getChrToIndexMap();
        Map<Integer, String> indexToChr = pe.getIndexToChrMap();

        System.out.println("=== Band Index Mapping for chromosome 16 ===");
        System.out.println("16p10 index: " + chrToIndex.get("16p10"));
        System.out.println("16p11.1 index: " + chrToIndex.get("16p11.1"));
        System.out.println("16p11.2 index: " + chrToIndex.get("16p11.2"));
        System.out.println("16p12.1 index: " + chrToIndex.get("16p12.1"));
        System.out.println("16p13.11 index: " + chrToIndex.get("16p13.11"));
        System.out.println("16p13.3 index: " + chrToIndex.get("16p13.3"));
        System.out.println("16q10 index: " + chrToIndex.get("16q10"));
        System.out.println("16q22.1 index: " + chrToIndex.get("16q22.1"));
        System.out.println("16q22.3 index: " + chrToIndex.get("16q22.3"));

        System.out.println("\n=== Testing getAllBandsBetween for 16p11->16q22 ===");
        List<String> bands = BandRangeCalculator.getAllBandsBetween("16p11", "16q22");
        System.out.println("Result bands: " + bands);
        System.out.println("Contains 16p10? " + bands.contains("16p10"));
        System.out.println("Contains 16q10? " + bands.contains("16q10"));

        System.out.println("\n=== Testing getAllBandsBetween for 16p13->16pter ===");
        List<String> bands2 = BandRangeCalculator.getAllBandsBetween("16p13", "16pter");
        System.out.println("Result bands: " + bands2);

        System.out.println("\n=== Testing getAllBandsBetween for 16p13->16p11 ===");
        List<String> bands3 = BandRangeCalculator.getAllBandsBetween("16p13", "16p11");
        System.out.println("Result bands: " + bands3);
        System.out.println("Contains 16p12.1? " + bands3.contains("16p12.1"));
        System.out.println("Contains 16p10? " + bands3.contains("16p10"));
    }
}
