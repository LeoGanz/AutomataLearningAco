package ganz.leonard.automatalearning.automata.probability;

import java.util.Map;
import java.util.stream.Collectors;

public class ProbabilityUtil {

  /**
   * Normalize all probabilities so the sum of all probabilities is 1. This method does not mutate
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
   *     between 0 and 1 and must be normalized
   * @param <K> key type of the Map. Can be anything
   * @return sample from the map chosen according to the provided probabilities
   * @see #normalizeProbabilities(Map)
   */
  // inspired by https://stackoverflow.com/a/9330667
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
}
