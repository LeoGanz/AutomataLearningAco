package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.Util;
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
  private static final double INIT_PHEROMONE = 1;
  private final Map<T, Double> pheromones;

  public PheromoneTransition() {
    this.pheromones = new HashMap<>();
    // used for lazy init of pheromones; tracks decays
    pheromones.put(null, INIT_PHEROMONE);
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
    pheromones.put(letter, pheromones.get(letter) * 1.2);
  }

  public void decay() {
    // negative feedback / pheromone decay for all transitions
    pheromones.keySet().forEach(letter -> pheromones.put(letter, pheromones.get(letter) / 2.0));
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

    // Doubles have to be strictly compared to ensure transitivity and a consistent hashcode
    return Objects.equals(pheromones, that.pheromones);
  }

  @Override
  public int hashCode() {
    return pheromones != null ? pheromones.hashCode() : 0;
  }
}
