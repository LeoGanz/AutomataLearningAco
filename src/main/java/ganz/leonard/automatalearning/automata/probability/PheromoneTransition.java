package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Rules for a transition between two states. Defines probability of transiting for every letter of
 * the alphabet.
 *
 * @param <T> type used as alphabet
 */
public class PheromoneTransition<T> {
  public static final Function<Double, Double> SIGMOID = val -> val / (1 + Math.abs(val));
  // transitions with same values in pheromones and probs are not necessarily the same transitions
  private final UUID uuid;
  private final Map<T, Double> pheromones;
  private final Map<T, Double> probs;
  private final AutomataLearningOptions options;

  public PheromoneTransition(AutomataLearningOptions options) {
    uuid = UUID.randomUUID();
    this.options = options;
    pheromones = new HashMap<>();
    probs = new HashMap<>();
    // used for lazy init of pheromones; tracks decays
    pheromones.put(null, (double) options.initialPheromones());
  }

  public PheromoneTransition(PheromoneTransition<T> original, AutomataLearningOptions options) {
    uuid = UUID.randomUUID();
    pheromones = new HashMap<>(original.pheromones);
    probs = new HashMap<>(original.probs);
    this.options = options;
  }

  public double learnFunction(T letter) {
    ensureInit(letter);
    double pheromoneVal = pheromones.get(letter);
    double prevProb = probs.get(letter);
    double factor = SIGMOID.apply(pheromoneVal); // in [-1 ;1  ]
    factor /= 10; // in [-.1; .1]
    factor += 1; // in [0.9;1.1]
    return Math.max(0.01, factor * prevProb);
  }

  public double getTransitionProbability(T letter) {
    Objects.requireNonNull(letter);
    ensureInit(letter);
    return probs.get(letter);
  }

  public void updateDefaultProbability(int noNeighbors) {
    probs.put(null, 1.0 / noNeighbors);
  }

  /**
   * Call after distributing pheromones! <br>
   * Call only once per letter even if one ant visited this transition multiple times!
   *
   * @param letter symbol for which to update the transition probability
   */
  public void setTransitionProbability(T letter, double prob) {
    probs.put(letter, prob);
  }

  private void ensureInit(T letter) {
    if (!probs.containsKey(null)) {
      throw new IllegalStateException("Default transition probability has never been updated");
    }
    if (!pheromones.containsKey(letter)) {
      pheromones.put(letter, pheromones.get(null));
    }
    if (!probs.containsKey(letter)) {
      probs.put(letter, probs.get(null));
    }
  }

  public double getPheromoneFor(T letter) {
    Objects.requireNonNull(letter);
    ensureInit(letter);
    return pheromones.get(letter);
  }

  public void pheromoneFeedback(T letter, boolean positive) {
    Objects.requireNonNull(letter);
    ensureInit(letter);
    // update pheromones according to some formula
    double newVal =
        positive
            ? pheromones.get(letter) + options.feedbackFactor()
            : pheromones.get(letter) - options.feedbackFactor();
    pheromones.put(letter, newVal);
  }

  public void decay() {
    // negative feedback / pheromone decay for all transitions
    pheromones
        .keySet()
        .forEach(letter -> pheromones.put(letter, pheromones.get(letter) * options.decayFactor()));
  }

  public Collection<T> getKnownLetters() {
    return pheromones.keySet().stream().filter(Objects::nonNull).collect(Collectors.toSet());
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

    if (!Objects.equals(uuid, that.uuid)) {
      return false;
    }
    if (!Objects.equals(pheromones, that.pheromones)) {
      return false;
    }
    if (!Objects.equals(probs, that.probs)) {
      return false;
    }
    return Objects.equals(options, that.options);
  }

  @Override
  public int hashCode() {
    int result = uuid.hashCode();
    result = 31 * result + pheromones.hashCode();
    result = 31 * result + probs.hashCode();
    result = 31 * result + (options != null ? options.hashCode() : 0);
    return result;
  }
}
