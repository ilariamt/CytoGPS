import business.BandRangeCalculator;
import java.util.*;

public class TestBandRanges {
    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        // Test 1: Same arm segment (p arm)
        System.out.println("=== Test 1: Same arm segment (16p13->16p11) ===");
        List<String> result1 = BandRangeCalculator.getAllBandsBetween("16p13", "16p11");
        System.out.println("Result: " + result1);
        boolean test1 = result1.contains("16p12.1") && result1.contains("16p11.1") &&
                        result1.contains("16p13.11") && !result1.contains("16p10");
        System.out.println("Contains p12.1, p11.1, p13.11 but not p10: " + (test1 ? "PASS" : "FAIL"));
        if (test1) passed++; else failed++;

        // Test 2: Same arm segment (q arm)
        System.out.println("\n=== Test 2: Same arm segment (9q21->9q34) ===");
        List<String> result2 = BandRangeCalculator.getAllBandsBetween("9q21", "9q34");
        System.out.println("Result: " + result2);
        boolean test2 = result2.contains("9q22.1") && result2.contains("9q31.1") &&
                        !result2.contains("9q10") && !result2.contains("9p10");
        System.out.println("Contains q22.1, q31.1 but not q10/p10: " + (test2 ? "PASS" : "FAIL"));
        if (test2) passed++; else failed++;

        // Test 3: Cross-arm segment (p to q)
        System.out.println("\n=== Test 3: Cross-arm segment (16p11->16q22) ===");
        List<String> result3 = BandRangeCalculator.getAllBandsBetween("16p11", "16q22");
        System.out.println("Result: " + result3);
        boolean test3 = result3.contains("16p10") && result3.contains("16p11.1") &&
                        result3.contains("16q10") && result3.contains("16q22.1") &&
                        !result3.contains("16p12.1") && !result3.contains("16p13.11");
        System.out.println("Contains p10, p11, q10, q22 but not p12/p13: " + (test3 ? "PASS" : "FAIL"));
        if (test3) passed++; else failed++;

        // Test 4: Cross-arm segment (q to p) - less common but should work
        System.out.println("\n=== Test 4: Cross-arm segment (16q21->16p11) ===");
        List<String> result4 = BandRangeCalculator.getAllBandsBetween("16q21", "16p11");
        System.out.println("Result: " + result4);
        boolean test4 = result4.contains("16p10") && result4.contains("16p11.1") &&
                        result4.contains("16q10") && result4.contains("16q21");
        System.out.println("Contains p10, p11, q10, q21: " + (test4 ? "PASS" : "FAIL"));
        if (test4) passed++; else failed++;

        // Test 5: With telomere (pter to band)
        System.out.println("\n=== Test 5: pter->p21 (6pter->6p21) ===");
        List<String> result5 = BandRangeCalculator.getAllBandsBetween("6pter", "6p21");
        System.out.println("Result: " + result5);
        boolean test5 = result5.contains("6p25.3") && result5.contains("6p21.1") &&
                        !result5.contains("6p10") && !result5.contains("6q10");
        System.out.println("Contains p25.3, p21.1 but not p10/q10: " + (test5 ? "PASS" : "FAIL"));
        if (test5) passed++; else failed++;

        // Test 6: With telomere (band to qter)
        System.out.println("\n=== Test 6: p21->qter (6p21->6qter) ===");
        List<String> result6 = BandRangeCalculator.getAllBandsBetween("6p21", "6qter");
        System.out.println("Result: " + result6);
        boolean test6 = result6.contains("6p21.1") && result6.contains("6p10") &&
                        result6.contains("6q10") && result6.contains("6q27");
        System.out.println("Contains p21.1, p10, q10, q27: " + (test6 ? "PASS" : "FAIL"));
        if (test6) passed++; else failed++;

        // Test 7: With telomere reversed (qter to band)
        System.out.println("\n=== Test 7: qter->q22 (16qter->16q22) ===");
        List<String> result7 = BandRangeCalculator.getAllBandsBetween("16qter", "16q22");
        System.out.println("Result: " + result7);
        boolean test7 = result7.contains("16q24.3") && result7.contains("16q22.1") &&
                        !result7.contains("16q10") && !result7.contains("16p10");
        System.out.println("Contains q24.3, q22.1 but not q10/p10: " + (test7 ? "PASS" : "FAIL"));
        if (test7) passed++; else failed++;

        // Test 8: With telomere reversed (band to pter)
        System.out.println("\n=== Test 8: p13->pter (16p13->16pter) ===");
        List<String> result8 = BandRangeCalculator.getAllBandsBetween("16p13", "16pter");
        System.out.println("Result: " + result8);
        boolean test8 = result8.contains("16p13.11") && result8.contains("16p13.3") &&
                        !result8.contains("16p12.1") && !result8.contains("16p10");
        System.out.println("Contains p13.11, p13.3 but not p12/p10: " + (test8 ? "PASS" : "FAIL"));
        if (test8) passed++; else failed++;

        // Test 9: Full arm (getAllBandsInArm)
        System.out.println("\n=== Test 9: getAllBandsInArm(16p) ===");
        List<String> result9 = BandRangeCalculator.getAllBandsInArm("16p");
        System.out.println("Result: " + result9);
        boolean test9 = result9.contains("16p10") && result9.contains("16p11.1") &&
                        result9.contains("16p13.3") && !result9.contains("16q10");
        System.out.println("Contains p10, p11.1, p13.3 but not q10: " + (test9 ? "PASS" : "FAIL"));
        if (test9) passed++; else failed++;

        // Test 10: Simple same-band range
        System.out.println("\n=== Test 10: Simple range (9q21.11->9q21.33) ===");
        List<String> result10 = BandRangeCalculator.getAllBandsBetween("9q21.11", "9q21.33");
        System.out.println("Result: " + result10);
        boolean test10 = result10.contains("9q21.11") && result10.contains("9q21.2") &&
                         result10.contains("9q21.33") && !result10.contains("9q22.1");
        System.out.println("Contains 9q21.* subbands but not q22: " + (test10 ? "PASS" : "FAIL"));
        if (test10) passed++; else failed++;

        System.out.println("\n=== SUMMARY ===");
        System.out.println("Passed: " + passed + "/" + (passed + failed));
        System.out.println("Failed: " + failed + "/" + (passed + failed));
        if (failed > 0) {
            System.exit(1);
        }
    }
}
