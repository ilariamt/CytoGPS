import business.BiologicalOutcome;
import business.FinalResult;
import business.ParseEvent;
import main.KaryotypeRunner;
import java.util.*;

public class TestFullKaryotype {
    public static void main(String[] args) {
        String karyotype = "48,XX,der(6)(16qter->16q22::16p13->16p11::6p21->6qter),+del(9)(q21q34),der(16)(6pter->6p21::16p11->16q22::16p13->16pter),+22[9]";

        System.out.println("Karyotype: " + karyotype);

        FinalResult result = KaryotypeRunner.getFinalResult(karyotype);

        if (result.getBiologicalOutcomeList().isEmpty()) {
            System.out.println("ERROR: No outcome!");
            return;
        }

        BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
        List<List<Integer>> lgf = outcome.getKaryotypeLGF();
        List<Integer> losses = lgf.get(0);
        List<Integer> gains = lgf.get(1);
        List<Integer> fusions = lgf.get(2);

        Map<Integer, String> indexToChr = new ParseEvent().getIndexToChrMap();

        System.out.println("\n=== ALL LOSSES ===");
        for (int i = 0; i < losses.size(); i++) {
            if (losses.get(i) > 0) {
                System.out.println(indexToChr.get(i) + "(L)");
            }
        }

        System.out.println("\n=== ALL GAINS ===");
        for (int i = 0; i < gains.size(); i++) {
            if (gains.get(i) > 0) {
                System.out.println(indexToChr.get(i) + "(G)");
            }
        }

        System.out.println("\n=== ALL FUSIONS ===");
        for (int i = 0; i < fusions.size(); i++) {
            if (fusions.get(i) > 0) {
                System.out.println(indexToChr.get(i) + "(F)");
            }
        }
    }
}
