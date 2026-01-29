import business.BiologicalOutcome;
import business.FinalResult;
import business.ParseEvent;
import main.KaryotypeRunner;
import toolkit.ISCN2016;
import java.util.*;

public class DebugBands {
    public static void main(String[] args) {
        String karyotype = "48,XX,der(6)(16qter->16q22::16p13->16p11::6p21->6qter),+del(9)(q21q34),der(16)(6pter->6p21::16p11->16q22::16p13->16pter),+22[9]";
        
        System.out.println("Full karyotype: " + karyotype);
        System.out.println("\nAnalysis:");
        System.out.println("  der(6) has: 6p21->6qter (proximal p arm + q arm from chr6)");
        System.out.println("  der(16) has: 6pter->6p21 (distal p arm from chr6)");
        System.out.println("  Combined: ALL of chr6 is present (just redistributed)");
        System.out.println("  Expected chr6: NO gains, NO losses for chr6 material");
        
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);
        
        if (result.getBiologicalOutcomeList().isEmpty()) {
            System.out.println("ERROR: No outcome!");
            return;
        }
        
        BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
        List<List<Integer>> lgf = outcome.getKaryotypeLGF();
        List<Integer> losses = lgf.get(0);
        List<Integer> gains = lgf.get(1);
        
        Map<String, Integer> chrToIndex = new ParseEvent().getChrToIndexMap();
        List<String> chr6Bands = ISCN2016.getChrBandsList("6");
        
        System.out.println("\n=== Chromosome 6 GAINS ===");
        int gainCount = 0;
        for (String band : chr6Bands) {
            Integer idx = chrToIndex.get(band.toLowerCase());
            if (idx != null && gains.get(idx) > 0) {
                System.out.println("  " + band + ": +" + gains.get(idx));
                gainCount++;
            }
        }
        System.out.println("Total chr6 gains: " + gainCount);
        
        System.out.println("\n=== Chromosome 6 LOSSES ===");
        int lossCount = 0;
        for (String band : chr6Bands) {
            Integer idx = chrToIndex.get(band.toLowerCase());
            if (idx != null && losses.get(idx) > 0) {
                System.out.println("  " + band + ": -" + losses.get(idx));
                lossCount++;
            }
        }
        System.out.println("Total chr6 losses: " + lossCount);
    }
}
