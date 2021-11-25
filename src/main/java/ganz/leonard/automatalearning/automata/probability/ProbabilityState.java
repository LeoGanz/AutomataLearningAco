package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.Util;
import ganz.leonard.automatalearning.automata.general.BasicState;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * State of an automaton. Can be an accepting state or not.
 *
 * @param <T> type used as alphabet
 */
public class ProbabilityState<T> extends BasicState<ProbabilityState<T>, T> {
  private final Map<ProbabilityState<T>, PheromoneTransition<T>> outgoingTransitions;
  private final AutomataLearningOptions options;

  public ProbabilityState(int id, boolean isAccepting, AutomataLearningOptions options) {
    super(id, isAccepting);
    this.options = options;
    outgoingTransitions = new HashMap<>();
  }

  public void addTransitionsTo(Collection<ProbabilityState<T>> states) {
    states.stream()
        .filter(state -> !outgoingTransitions.containsKey(state))
        .forEach(state -> outgoingTransitions.put(state, new PheromoneTransition<>(options)));
    outgoingTransitions
        .values()
        .forEach(transition -> transition.updateDefaultProb(outgoingTransitions.size()));
  }

  public void initTransitionsLikeIn(
      ProbabilityState<T> original, Map<Integer, ProbabilityState<T>> withTheseStates) {
    outgoingTransitions.clear();
    original.outgoingTransitions.forEach(
        (origTarget, origTrans) ->
            outgoingTransitions.put(
                withTheseStates.get(origTarget.getId()),
                new PheromoneTransition<>(origTrans, options)));
  }

  @Override
  public ProbabilityState<T> transit(T letter) {
    Map<ProbabilityState<T>, Double> probabilities = getNormalizedTransitionProbabilities(letter);
    if (probabilities.values().stream().mapToDouble(Double::doubleValue).sum()
        < Util.DOUBLE_COMPARISON_PRECISION) {
      // nowhere left to go as all probabilities are zero
      return null;
    }
    ProbabilityState<T> target = ProbabilityUtil.sample(probabilities);
    outgoingTransitions.get(target).setPrevProb(letter, probabilities.get(target));
    return target;
  }

  public Map<ProbabilityState<T>, Double> getNormalizedTransitionProbabilities(T letter) {
    double sumOfRawProbs =
        outgoingTransitions.values().stream()
            .mapToDouble(trans -> trans.getRawProbFor(letter))
            .sum();
    Map<ProbabilityState<T>, Double> probs =
        outgoingTransitions.entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getRawProbFor(letter) / sumOfRawProbs));
    return ProbabilityUtil.normalizeProbabilities(probs);
  }

  public Set<T> getUsedLetters() {
    return outgoingTransitions.values().stream()
        .flatMap(transition -> transition.getKnownLetters().stream())
        // .distinct() already distinct through set
        .collect(Collectors.toSet());
  }

  public PheromoneTransition<T> getTransitionTo(ProbabilityState<T> state) {
    return outgoingTransitions.get(state);
  }

  public Map<ProbabilityState<T>, PheromoneTransition<T>> getOutgoingTransitions() {
    return outgoingTransitions;
  }

  public AutomataLearningOptions getOptions() {
    return options;
  }
}
