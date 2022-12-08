package SequenceAlignmentBenchmark;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SequenceAlignmentUtils {
  public class IOHandler {

    // base string s1
    public String s1;

    // base string s2
    public String s2;

    // input FilePath
    public String inputFilePath;

    // output FilePath
    public String outputFilePath;

    private boolean isInteger(String strNum) {

      if (strNum == null) {
        return false;
      }
      try {
        int d = Integer.parseInt(strNum);
      } catch (NumberFormatException nfe) {
        return false;
      }
      return true;
    }


    public IOHandler(String inputFilePath, String outputFilePath) {
      this.inputFilePath = inputFilePath;
      this.outputFilePath = outputFilePath;

      try {
        FileInputStream fstream = new FileInputStream(inputFilePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        boolean isS1Init = false;
        String sPattern = "";
        while ((strLine = br.readLine()) != null)   {
          if (!isS1Init) {
            assert !isInteger(strLine);
            sPattern = strLine;
            isS1Init = true;
          } else {
            if (isInteger(strLine)) {
              int index = Integer.parseInt(strLine);
              sPattern = sPattern.substring(0, index + 1) + sPattern + sPattern.substring(index+1);
            } else {
              s1 = sPattern;
              sPattern = strLine;
            }
          }
        }
        s2 = sPattern;
        fstream.close();

        // logging
        System.out.printf("Finish load the inputFile: %s\n", inputFilePath);
      } catch (IOException e) {
        System.out.printf("Error happens in InputHandler constructor: %s\n", e);
      }
    }

    public void writeIntoOutputFile(int cost, String res1, String res2, double executionTimeMs,
                                    double memUsageKb) throws IOException {
      File f = new File(outputFilePath);
      FileOutputStream fos = new FileOutputStream(f);
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
      bw.write(String.valueOf(cost));
      bw.newLine();
      bw.write(res1);
      bw.newLine();
      bw.write(res2);
      bw.newLine();
      bw.write(String.valueOf(executionTimeMs));
      bw.newLine();
      bw.write(String.valueOf(memUsageKb));
      bw.close();
    }
  }


  // delta, gap penalty
  static public int DELTA = 30;

  static public IOHandler ioHandler;

  // alpha, mismatch penalty
  static public int[][] ALPHA = {
      {0, 110, 48, 94},
      {110, 0, 118, 48},
      {48, 118, 0, 110},
      {94, 48, 110, 0}
  };

  static private int getIndexFromCharacter(char a) {
    int idx;
    if (a == 'A') {
      idx = 0;
    } else if (a == 'C') {
      idx = 1;
    } else if (a == 'G') {
      idx = 2;
    } else {
      idx = 3;
    }
    return idx;
  }

  static public int getMismatchPenalty(char a, char b) {
    return ALPHA[getIndexFromCharacter(a)][getIndexFromCharacter(b)];
  }

  public static double getTimeInMilliseconds() {
    return System.nanoTime()/10e6;
  }

  public static double getMemoryInKB() {
    double total = Runtime.getRuntime().totalMemory();
    return (total-Runtime.getRuntime().freeMemory())/10e3;
  }

  public SequenceAlignmentUtils(String inputFilePath, String outputFilePath) {
    ioHandler = new IOHandler(inputFilePath, outputFilePath);
    System.out.printf("Inputting %s, result saved to %s\n", inputFilePath, outputFilePath);
  }

  public String[] getBaseString() {
    String[] res = {ioHandler.s1, ioHandler.s2};
    return res;
  }

  static public void writeResultIntoOutputFile(int cost, String res1, String res2, double totalTimeMs,
                                        double totalMemUsageKb) throws IOException {
    ioHandler.writeIntoOutputFile(cost, res1, res2, totalTimeMs, totalMemUsageKb);
  }

  public static class AlignmentResult {
    int optValue;

    String s1;

    String s2;

    public AlignmentResult(int optValue, String s1, String s2) {
      this.optValue = optValue;
      this.s1 = s1;
      this.s2 = s2;
    }

  }


}
