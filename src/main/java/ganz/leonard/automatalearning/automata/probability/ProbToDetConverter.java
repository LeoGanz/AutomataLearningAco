package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProbToDetConverter<T> {
  private final FeedbackAutomaton<T> automaton;
  private DeterministicFiniteAutomaton<T> dfa;

  public ProbToDetConverter(FeedbackAutomaton<T> automaton) {
    this.automaton = automaton;
  }

  private void constructTransitionsFor(ProbabilityState<T> probState) {
    Set<T> usedLetters =
        probState.getOutgoingTransitions().values().stream()
            .flatMap(transition -> transition.getKnownLetters().stream())
            // .distinct() already distinct in set
            .collect(Collectors.toSet());

    usedLetters.forEach(letter -> constructSingleTransition(probState, letter));
  }

  private void constructSingleTransition(ProbabilityState<T> probState, T letter) {
    ProbabilityState<T> successor =
        probState.getOutgoingTransitions().entrySet().stream()
            .max(Comparator.comparingDouble(entry -> entry.getValue().getRawProbabilityFor(letter)))
            .orElseGet(() -> new AbstractMap.SimpleEntry<>(null, null)) // else should not occur
            .getKey();
    // what about low probabilities and ~ zero prob? would no transition be better?
    if (successor != null) {
      DeterministicState<T> successorDfa = dfa.getAllStates().get(successor.getId());
      dfa.getAllStates().get(probState.getId()).initTransitions(Map.of(letter, successorDfa));
    }
  }

  /**
   * Construct an {@link DeterministicFiniteAutomaton} with the same state structure and choose the
   * most likely transitions.
   *
   * @return the newly constructed automaton
   */
  public DeterministicFiniteAutomaton<T> convert() {
    buildAutomatonSkeleton();
    automaton.getAllStates().values().forEach(this::constructTransitionsFor);
    return dfa;
  }

  private void buildAutomatonSkeleton() {
    Map<Integer, DeterministicState<T>> newStates =
        automaton.getAllStates().entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry ->
                        new DeterministicState<>(
                            entry.getValue().getId(), entry.getValue().isAccepting())));
    DeterministicState<T> start = newStates.get(automaton.getStartState().getId());
    dfa = new DeterministicFiniteAutomaton<>(newStates, start);
  }
}
