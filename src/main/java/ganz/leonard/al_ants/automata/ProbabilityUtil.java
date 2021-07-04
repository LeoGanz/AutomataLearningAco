package ganz.leonard.al_ants.automata;

import java.util.Map;
import java.util.stream.Collectors;

public class ProbabilityUtil {

  /**
   * Normalize all probabilities so the some of all probabilities is 1. This method does not mutate
   * the parameter.
   *
   * @param rawProbabilities mapping of things to probabilities. Probabilities have to be >= 0
   * @param <K> key type of the Map. Can be anything
   * @return new map with normalized probabilities
   * @throws IllegalArgumentException if probabilities are not all at least 0
   */
  public static <K> Map<K, Double> normalizeProbabilities(Map<K, Double> rawProbabilities) {
    if (rawProbabilities.isEmpty()) {
      return Map.of();
    }
    if (rawProbabilities.values().stream().anyMatch(d -> d < 0)) {
      throw new IllegalArgumentException("All Probabilities must be >= 0");
    }
    double sum = rawProbabilities.values().stream().mapToDouble(Double::doubleValue).sum();
    if (sum == 0) {
      // ensures no division by 0 occurs
      return Map.copyOf(rawProbabilities);
    }
    return rawProbabilities.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() / sum));
  }

  /**
   * Chooses an element from the map whilst respecting the provided probabilities.
   *
   * @param elementsWithProbabilities mapping of things to probabilities. Probabilities have to be
   *     between 0 and 1 an must be normalized
   * @param <K> key type of the Map. Can be anything
   * @return sample from the map chosen according to the provided probabilities
   * @see #normalizeProbabilities(Map)
   */
  public static <K> K sample(Map<K, Double> elementsWithProbabilities) {
    double prob = Math.random();
    double cumulativeProb = 0;
    for (Map.Entry<K, Double> keyWithProb : elementsWithProbabilities.entrySet()) {
      cumulativeProb += keyWithProb.getValue();
      if (prob <= cumulativeProb) {
        return keyWithProb.getKey();
      }
    }
    throw new IllegalArgumentException(
        "All probabilities 0 or invalid probabilities (e.g. sum != 1)");
  }

  /**
   * Generate a pseudo-random number between bounds. Probability decreases linearly between lower
   * and upper bound (favors small numbers). Inverse Transform Sampling.
   *
   * @param lower lower bound (inclusive)
   * @param upper upper bound (exclusive)
   * @return a pseudo-random number
   */
  // see https://gamedev.stackexchange.com/questions/116832/random-number-in-a-range-biased-toward-the-low-end-of-the-range
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
   * Generate a pseudo-random number. Probability decreases exponentially from zero (favors small numbers). Inverse Transform Sampling.
   *
   * @return a pseudo-random number
   */
  // see https://stackoverflow.com/questions/2106503/pseudorandom-number-generator-exponential-distribution
  // see https://en.wikipedia.org/wiki/Inverse_transform_sampling
  // see https://de.wikipedia.org/wiki/Exponentialverteilung#Zufallszahlen
  public static int sampleExpDist() {
    double lambda = 1.0/4;
    double distribution = Math.log(1 - Math.random()) / (-lambda);
    return (int) distribution;
  }
}
