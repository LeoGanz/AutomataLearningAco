package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.Util;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Rules for a transition between two states. Defines probability of transiting for every letter of
 * the alphabet.
 *
 * @param <T> type used as alphabet
 */
public class PheromoneTransition<T> {
  private final Map<T, Double> pheromones;
  private final AutomataLearningOptions options;

  public PheromoneTransition(AutomataLearningOptions options) {
    this.options = options;
    this.pheromones = new HashMap<>();
    // used for lazy init of pheromones; tracks decays
    pheromones.put(null, (double) options.initialPheromones());
  }

  public PheromoneTransition(PheromoneTransition<T> original, AutomataLearningOptions options) {
    pheromones = new HashMap<>(original.pheromones);
    this.options = options;
  }

  public double getRawProbabilityFor(T letter) {
    Objects.requireNonNull(letter);
    ensureInit(letter);
    // calculate probability as some function of pheromone levels
    return pheromones.get(letter);
  }

  private void ensureInit(T letter) {
    if (!pheromones.containsKey(letter)) {
      pheromones.put(letter, pheromones.get(null));
    }
  }

  public void positivePheromoneFeedback(T letter) {
    Objects.requireNonNull(letter);
    ensureInit(letter);
    // update pheromones according to some formula
    pheromones.put(letter, pheromones.get(letter) * options.positiveFeedbackFactor());
  }

  public void decay() {
    // negative feedback / pheromone decay for all transitions
    pheromones
        .keySet()
        .forEach(
            letter ->
                pheromones.put(letter, pheromones.get(letter) * options.negativeFeedbackFactor()));
  }

  public Collection<T> getKnownLetters() {
    return pheromones.keySet().stream().filter(Objects::nonNull).collect(Collectors.toSet());
  }

  public boolean equalsApprox(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PheromoneTransition<?> that = (PheromoneTransition<?>) o;

    if (!Objects.equals(options, that.options)) {
      return false;
    }

    // Compare map containing doubles with tolerance
    if (pheromones == null) {
      return that.pheromones == null;
    }
    if (that.pheromones == null) {
      return false;
    }
    if (pheromones.size() != that.pheromones.size()) {
      return false;
    }
    if (!Objects.equals(pheromones.keySet(), that.pheromones.keySet())) {
      return false;
    }

    return pheromones.keySet().stream()
        .allMatch(key -> Util.doubleApproxEquals(pheromones.get(key), that.pheromones.get(key)));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PheromoneTransition<?> that = (PheromoneTransition<?>) o;

    if (!Objects.equals(pheromones, that.pheromones)) {
      return false;
    }
    return Objects.equals(options, that.options);
  }

  @Override
  public int hashCode() {
    int result = pheromones != null ? pheromones.hashCode() : 0;
    result = 31 * result + (options != null ? options.hashCode() : 0);
    return result;
  }
}
