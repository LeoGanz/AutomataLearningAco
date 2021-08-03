package ganz.leonard.automatalearning.language;

public class RngUtil {

  /**
   * Generate a pseudo-random number between bounds. Probability decreases linearly between lower
   * and upper bound (favors small numbers). Inverse Transform Sampling.
   *
   * @param lower lower bound (inclusive)
   * @param upper upper bound (exclusive)
   * @return a pseudo-random number
   */
  // see
  // https://gamedev.stackexchange.com/questions/116832/random-number-in-a-range-biased-toward-the-low-end-of-the-range
  public static int sampleLinearDist(int lower, int upper) {
    if (lower < 0 || upper < 0) {
      throw new IllegalArgumentException("Bounds must be >= 0");
    }
    if (lower > upper) {
      throw new IllegalArgumentException("Lower bound must be smaller than upper bound");
    }
    if (lower == upper) {
      return lower;
    }

    double distribution = 1 - Math.sqrt(1 - Math.random()); // 0 to 1
    double sample = Math.floor(distribution * (upper - lower) + lower);
    return (int) sample;
  }

  /**
   * Generate a pseudo-random number. Probability decreases exponentially from zero (favors small
   * numbers). Inverse Transform Sampling.
   *
   * @return a pseudo-random number
   */
  // see
  // https://stackoverflow.com/questions/2106503/pseudorandom-number-generator-exponential-distribution
  // see https://en.wikipedia.org/wiki/Inverse_transform_sampling
  // see https://de.wikipedia.org/wiki/Exponentialverteilung#Zufallszahlen
  public static int sampleExpDist() {
    double lambda = 1.0 / 4;
    double distribution = Math.log(1 - Math.random()) / (-lambda);
    return (int) distribution;
  }
}
