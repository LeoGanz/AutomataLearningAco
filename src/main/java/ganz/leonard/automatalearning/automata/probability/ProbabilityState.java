package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.Util;
import ganz.leonard.automatalearning.automata.general.BasicState;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
        .forEach(transition -> transition.updateDefaultProbability(outgoingTransitions.size()));
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
    Map<ProbabilityState<T>, Double> probabilities = collectTransitionProbabilities(letter);
    if (probabilities.values().stream().mapToDouble(Double::doubleValue).sum()
        < Util.DOUBLE_COMPARISON_PRECISION) {
      // nowhere left to go as all probabilities are zero
      return null;
    }
    return ProbabilityUtil.sample(probabilities);
  }

  public Map<ProbabilityState<T>, Double> collectTransitionProbabilities(T letter) {
    return outgoingTransitions.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, entry -> entry.getValue().getTransitionProbability(letter)));
  }

  public void updateProbabilities(T letter) {
    Map<PheromoneTransition<T>, Double> newTransitionProbs =
        ProbabilityUtil.normalizeProbabilities(
            outgoingTransitions.values().stream()
                .collect(
                    Collectors.toMap(Function.identity(), trans -> trans.learnFunction(letter))));
    newTransitionProbs.forEach((trans, prob) -> trans.setTransitionProbability(letter, prob));
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
