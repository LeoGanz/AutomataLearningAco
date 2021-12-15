package ganz.leonard.automatalearning.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

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

  public static String padLeft(String s, int length) {
    return String.format("%1$" + length + "s", s);
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

  /**
   * Determine the x th highest element in a stream or the highest element for smaller streams.
   *
   * @param x offset in reverse sorted stream
   * @param stream stream containing the input data (unsorted)
   * @param <R> type of input data, has to be sortable
   * @return xth highest element or highest of input data if the stream contains less than x
   *     elements
   * @throws NoSuchElementException if stream is empty
   */
  public static <R extends Comparable<R>> R getXthHighestVal(int x, Stream<R> stream) {
    return stream
        .sorted(Comparator.reverseOrder())
        .limit(x)
        .reduce((fst, snd) -> snd)
        .orElseThrow(() -> new NoSuchElementException("No xth highest element in empty stream"));
  }

  /**
   * Same functionality as {@link List#indexOf} but with reference equality.
   */
  public static <T> int indexOfReferenceEquality(List<T> list, T elem) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i) == elem) { // reference equality
        return i;
      }
    }
    return -1;
  }
}
