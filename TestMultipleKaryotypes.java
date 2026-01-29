import business.BiologicalOutcome;
import business.FinalResult;
import business.ParseEvent;
import main.KaryotypeRunner;
import java.util.*;

public class TestMultipleKaryotypes {
    public static void main(String[] args) {
        // Test cases with expected outcomes
        String[] karyotypes = {
            // Original problematic case
            "48,XX,der(6)(16qter->16q22::16p13->16p11::6p21->6qter),+del(9)(q21q34),der(16)(6pter->6p21::16p11->16q22::16p13->16pter),+22[9]",
            // Simple deletion
            "46,XY,del(13)(q14q21)",
            // Simple translocation
            "46,XX,t(9;22)(q34;q11)",
            // Derivative with single base chromosome
            "46,XX,der(13)(13pter->13q10::15q10->15qter)"
        };

        for (int k = 0; k < karyotypes.length; k++) {
            System.out.println("=== Test " + (k+1) + " ===");
            System.out.println("Karyotype: " + karyotypes[k]);

            try {
                FinalResult result = KaryotypeRunner.getFinalResult(karyotypes[k]);

                if (result.getBiologicalOutcomeList().isEmpty()) {
                    System.out.println("No outcome - possibly undecoded");
                    continue;
                }

                BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
                List<List<Integer>> lgf = outcome.getKaryotypeLGF();
                List<Integer> losses = lgf.get(0);
                List<Integer> gains = lgf.get(1);
                List<Integer> fusions = lgf.get(2);

                Map<Integer, String> indexToChr = new ParseEvent().getIndexToChrMap();

                // Count non-zero entries
                int lossCount = 0, gainCount = 0, fusionCount = 0;
                for (int i = 0; i < losses.size(); i++) {
                    if (losses.get(i) > 0) lossCount++;
                    if (gains.get(i) > 0) gainCount++;
                    if (fusions.get(i) > 0) fusionCount++;
                }

                System.out.println("Losses: " + lossCount + " bands, Gains: " + gainCount + " bands, Fusions: " + fusionCount + " bands");

                // Print detailed system if available
                if (!outcome.getDetailedSystem().isEmpty()) {
                    System.out.println("Detailed system: " + outcome.getDetailedSystem());
                }

                System.out.println("PASS: Parsed successfully\n");

            } catch (Exception e) {
                System.out.println("FAIL: " + e.getMessage());
                e.printStackTrace();
                System.out.println();
            }
        }

        // Special test for the original problematic case
        System.out.println("=== Special verification for original case ===");
        String karyotype = "48,XX,der(6)(16qter->16q22::16p13->16p11::6p21->6qter),+del(9)(q21q34),der(16)(6pter->6p21::16p11->16q22::16p13->16pter),+22[9]";
        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);
        BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
        List<Integer> losses = outcome.getKaryotypeLGF().get(0);
        Map<String, Integer> chrToIndex = new ParseEvent().getChrToIndexMap();

        // Check that 16p10 is NOT lost
        Integer idx16p10 = chrToIndex.get("16p10");
        if (idx16p10 != null && losses.get(idx16p10) > 0) {
            System.out.println("FAIL: 16p10 is incorrectly marked as lost!");
        } else {
            System.out.println("PASS: 16p10 is NOT marked as lost (correct)");
        }

        // Check that 16p12.* ARE lost (the gap between the segments)
        Integer idx16p12_1 = chrToIndex.get("16p12.1");
        if (idx16p12_1 != null && losses.get(idx16p12_1) > 0) {
            System.out.println("PASS: 16p12.1 is correctly marked as lost");
        } else {
            System.out.println("FAIL: 16p12.1 should be lost but is not");
        }
    }
}
