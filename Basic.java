package SequenceAlignmentBenchmark;

import java.io.IOException;

import static SequenceAlignmentBenchmark.SequenceAlignmentUtils.getMemoryInKB;
import static SequenceAlignmentBenchmark.SequenceAlignmentUtils.getTimeInMilliseconds;

public class Basic {
  static public byte IS_MISMATCH = 1;
  static public byte IS_GAP = 2;

  private static class BasicMatchingPoint {
    // 1 -> mismatch, 2 -> gap
    private byte matchingPattern;

    // 0 -> no previous step 1 -> [-1, -1], 2 -> [0, -1], 3 -> [-1, 0]
    private byte previousStep;

    public int penaltyValue;

    public BasicMatchingPoint(byte matchingPattern, byte previousStep, int penaltyValue) {
      this.matchingPattern = matchingPattern;
      this.previousStep = previousStep;
      this.penaltyValue = penaltyValue;
    }

    public byte[] getPreviousSteps() {
      if (previousStep == 0){
        return new byte[]{0, 0, 0};
      } else if (previousStep == 1) {
        return new byte[]{1, -1, -1};
      } else if (previousStep == 2) {
        return new byte[]{2, 0, -1};
      }
      return new byte[]{3, -1, 0};
    }

    public boolean isMismatch() {
      return this.matchingPattern == IS_MISMATCH;
    }
  }

  public static int[] getCostArr(BasicMatchingPoint[][] dp) {
    int[] res = new int[dp[0].length];
    for (int i = 0; i<dp[0].length; i++) {
      res[i] = dp[dp.length-1][i].penaltyValue;
    }
    return res;
  }
  public static SequenceAlignmentUtils.AlignmentResult basicDP(String s1, String s2) {
    int n = s1.length()+1;
    int m = s2.length()+1;
    BasicMatchingPoint[][] dp = new BasicMatchingPoint[n][m];

    // initialization
    dp[0][0] = new BasicMatchingPoint((byte) 0, (byte) 0, 0);
    for (int i = 1; i < n; i++) {
      dp[i][0] = new BasicMatchingPoint((byte) 2, (byte) 3, i * SequenceAlignmentUtils.DELTA);
    }
    for (int i = 1; i < m; i++) {
      dp[0][i] = new BasicMatchingPoint((byte) 2, (byte) 2,
              i * SequenceAlignmentUtils.DELTA);
    }

    for (int i = 1; i < n; i++) {
      char c1 = s1.charAt(i-1);
      for (int j = 1; j < m; j++) {
        char c2 = s2.charAt(j-1);

        int val1 = SequenceAlignmentUtils.getMismatchPenalty(c1, c2) + dp[i-1][j-1].penaltyValue;
        int val2 = SequenceAlignmentUtils.DELTA + dp[i][j-1].penaltyValue;
        int val3 = SequenceAlignmentUtils.DELTA + dp[i-1][j].penaltyValue;
        byte minStep = 1;
        int minVal = val1;
        if (minVal > val2) {
          minVal = val2;
          minStep = 2;
        }
        if (minVal > val3) {
          minVal = val3;
          minStep = 3;
        }
        byte matchPattern = IS_MISMATCH;
        if (minStep > 1) {
          matchPattern = IS_GAP;
        }
        dp[i][j] = new BasicMatchingPoint(matchPattern, minStep, minVal);
      }
    }

    // collect optValue and optSolution
    int optValue = dp[n-1][m-1].penaltyValue;
    StringBuilder resS1 = new StringBuilder();
    StringBuilder resS2 = new StringBuilder();
    int p = n-1, q = m-1;
    while (p != 0 || q != 0) {
      char c1, c2;
      if (p == 0 || q == 0) {
        if (p == 0) {
          c1 = '_';
          c2 = s2.charAt(q-1);
        } else {
          c1 = s1.charAt(p-1);
          c2 = '_';
        }
      } else {
        c1 = s1.charAt(p-1);
        c2 = s2.charAt(q-1);
      }

      byte[] prevStep = dp[p][q].getPreviousSteps();
      if (dp[p][q].isMismatch()) {
        resS1.insert(0, c1);
        resS2.insert(0, c2);
      } else {
        if (prevStep[0] == 2) {
          resS1.insert(0, '_');
          resS2.insert(0, c2);
        } else {
          resS1.insert(0, c1);
          resS2.insert(0, '_');
        }
      }
      p += prevStep[1];
      q += prevStep[2];
    }
//    int[] costArr = getCostArr(dp);
    return new SequenceAlignmentUtils.AlignmentResult(optValue, resS1.toString(), resS2.toString());
  }
  public static void main(String[] args) throws IOException {

    // doing IO and string generation, we ignore the memory consumption and time of this part
    SequenceAlignmentUtils sequenceAlignmentUtils = new SequenceAlignmentUtils(args[0], args[1]);

    double beforeUsedMem= getMemoryInKB();
    double startTime = getTimeInMilliseconds();

    String[] baseString = sequenceAlignmentUtils.getBaseString();
    SequenceAlignmentUtils.AlignmentResult res = basicDP(baseString[0], baseString[1]);

    double afterUsedMem = getMemoryInKB();
    double endTime = getTimeInMilliseconds();
    double totalMemUsageKb =  afterUsedMem-beforeUsedMem;
    double totalTimeMs =  endTime - startTime;
    SequenceAlignmentUtils.writeResultIntoOutputFile(res.optValue, res.s1, res.s2,
        totalTimeMs,
        totalMemUsageKb);
  }

}
