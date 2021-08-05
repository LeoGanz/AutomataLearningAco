package ganz.leonard.automatalearning;

public class Util {
  public static int nrOfDigits(int i) {
    if (i == 0) {
      return 1;
    }
    if (i < 0) {
      return nrOfDigits(-i) + 1;
    }
    return (int) Math.ceil(Math.log10(i + 1));
  }

  private static final double PRECISION = 0.00001;

  public static boolean doubleApproxEquals(double d1, double d2) {
    return Math.abs(d1 - d2) < PRECISION;
  }
}
