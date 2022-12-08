package SequenceAlignmentBenchmark;

import java.io.IOException;

import static SequenceAlignmentBenchmark.SequenceAlignmentUtils.*;

public class Efficient {

    private static int[] dpCostArr(String s1, String s2) {
        int n = s1.length()+1;
        int m = s2.length()+1;
        int[][] dp = new int[2][m];
        s1 = "#"+s1;
        s2 = "#"+s2;

        // initialization
        dp[0][0] = 0;
        for(int i = 1; i<m; i++) {
            dp[0][i] = i * DELTA;
        }

        for (int i = 1; i < n; i++) {
            char c1 = s1.charAt(i);
            int currentRow = i%2;
            int previousRow = (i-1)%2;
            dp[currentRow][0] = DELTA * i;
            for (int j = 1; j < m; j++) {
                char c2 = s2.charAt(j);

                dp[currentRow][j] = getMismatchPenalty(c1, c2) + dp[previousRow][j-1];
                dp[currentRow][j] = Integer.min(dp[currentRow][j], DELTA + dp[currentRow][j-1]);
                dp[currentRow][j] = Integer.min(dp[currentRow][j], DELTA + dp[previousRow][j]);
            }
        }
        int lastRow = (n-1) % 2;
        return dp[lastRow];
    }



    public static SequenceAlignmentUtils.AlignmentResult efficientDP(String x, String y) {
        if ((x.length() == 0) && (y.length() == 0)) {
            return new AlignmentResult(0, "", "");
        }

        if (x.length() <= 1 || y.length() <= 1) {
            return Basic.basicDP(x, y);
        }

        // divide x
        int m = x.length() / 2;
        String xl = x.substring(0, m);

        // reverse xr
        String xr = x.substring(m);
        StringBuilder xrb = new StringBuilder(xr);
        xrb.reverse();
        // reverse y
        StringBuilder yrb = new StringBuilder(y);
        yrb.reverse();

        // calculate the costs
        int arrLength = y.length()+1;
        int[] costArrLeft = dpCostArr(xl, y);
//        Basic.basicDP(xl, y);
        int[] costArrRight = dpCostArr(xrb.toString(), yrb.toString());
//        Basic.basicDP(xrb.toString(), yrb.toString());

        int[] totalCostArr = new int[arrLength];

        for (int i = 0; i<arrLength; i++) {
            totalCostArr[i] = costArrLeft[i] + costArrRight[arrLength - i - 1];
        }

        // choose the smallest cost value and index
        int minCost = totalCostArr[0];
        int minCostIdx = 0;
        for (int i = 1; i< arrLength; i++) {
            if (totalCostArr[i] < minCost) {
                minCostIdx = i;
                minCost = totalCostArr[i];
            }
        }


        String yl = y.substring(0, minCostIdx);
        String yr = y.substring(minCostIdx);
        // divide and conquer
        AlignmentResult resL = efficientDP(xl, yl);
        AlignmentResult resR = efficientDP(xr, yr);
        return new AlignmentResult(resL.optValue + resR.optValue, resL.s1 + resR.s1, resL.s2 + resR.s2);
    }
    public static void main(String[] args) throws IOException {

        // doing IO and string generation, we ignore the memory consumption and time of this part
        SequenceAlignmentUtils sequenceAlignmentUtils = new SequenceAlignmentUtils(args[0], args[1]);

        double beforeUsedMem= getMemoryInKB();
        double startTime = getTimeInMilliseconds();

        String[] baseString = sequenceAlignmentUtils.getBaseString();
        AlignmentResult res = efficientDP(baseString[0], baseString[1]);
        double afterUsedMem = getMemoryInKB();
        double endTime = getTimeInMilliseconds();
        double totalMemUsageKb =  afterUsedMem-beforeUsedMem;
        double totalTimeMs =  endTime - startTime;
        SequenceAlignmentUtils.writeResultIntoOutputFile(res.optValue, res.s1, res.s2, totalTimeMs, totalMemUsageKb);
    }
}
