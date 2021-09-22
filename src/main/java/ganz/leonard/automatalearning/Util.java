package ganz.leonard.automatalearning;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {

  public static final double DOUBLE_COMPARISON_PRECISION = 0.00001;

  private Util() {}

  // https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  public static String padRight(String s, int length) {
    return String.format("%-" + length + "s", s);
  }

  /**
   * Pads rounded number with spaces so the final length is number of non-decimal places + 1 +
   * <code>places</code>.
   *
   * @param value double to format
   * @param places number of decimal places
   * @return formatted double
   */
  public static String formatDouble(double value, int places) {
    double d = round(value, places);
    int digits = value < 1 ? 1 : (int) (Math.log10(d) + 1);
    return padRight(String.valueOf(d), digits + 1 + places);
  }

  /**
   * Format a double as percentage.
   *
   * @param value double to format
   * @param places number of decimal places to display
   * @return formatted double with percentage sign
   * @see #formatDouble
   */
  public static String formatPercentage(double value, int places) {
    return formatDouble(value * 100, 1) + "%";
  }

  public static int nrOfDigits(int i) {
    if (i == 0) {
      return 1;
    }
    if (i < 0) {
      return nrOfDigits(-i) + 1;
    }
    return (int) Math.ceil(Math.log10(i + 1));
  }

  public static boolean doubleApproxEquals(double d1, double d2) {
    return Math.abs(d1 - d2) < DOUBLE_COMPARISON_PRECISION;
  }

  public static int calcSubstringStart(String key, String line) {
    return line.indexOf(key) + key.length();
  }
}
