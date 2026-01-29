import business.BiologicalOutcome;
import business.FinalResult;
import main.KaryotypeRunner;
import java.util.List;

public class TestIntegration {
    public static void main(String[] args) {
        String test1 = "45,XY,del(2)(q32q37),del(7)(q31q36),der(8;17)(17pter->17p13::17q12->17q25::8p23->8q24::17p13->17q23::8q22->8qter),inv(16)(p13q22)[2]";
        String test2 = "48,XX,der(6)(16qter->16q22::16p13->16p11::6p21->6qter),+del(9)(q21q34),der(16)(6pter->6p21::16p11->16q22::16p13->16pter),+22[9]";
        
        System.out.println("=== Test 1: der(8;17) ===");
        showResults(test1);
        
        System.out.println("\n=== Test 2: der(6) and der(16) ===");
        showResults(test2);
    }
    
    private static void showResults(String karyotype) {
        try {
            FinalResult result = KaryotypeRunner.getFinalResult(karyotype);
            
            if (result.getBiologicalOutcomeList().isEmpty()) {
                System.out.println("FAILED - No outcome produced");
                return;
            }
            
            BiologicalOutcome outcome = result.getBiologicalOutcomeList().get(0);
            List<List<Integer>> lgf = outcome.getKaryotypeLGF();
            List<Integer> losses = lgf.get(0);
            List<Integer> gains = lgf.get(1);
            
            int lossCount = 0, gainCount = 0;
            for (int v : losses) if (v != 0) lossCount++;
            for (int v : gains) if (v != 0) gainCount++;
            
            System.out.println("LGF vectors: " + lossCount + " loss entries, " + gainCount + " gain entries");
            System.out.println("SUCCESS!");
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
