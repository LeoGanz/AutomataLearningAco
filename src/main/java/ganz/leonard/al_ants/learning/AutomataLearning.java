package ganz.leonard.al_ants.learning;

import ganz.leonard.al_ants.automata.probability.FeedbackAutomaton;
import ganz.leonard.al_ants.automata.probability.ProbabilityState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AutomataLearning<T> {
  private final FeedbackAutomaton<T> automaton;
  private final Map<List<T>, Boolean> inputWords;

  /**
   * Initialize a new automata learning setup. A graph of accepting and not accepting states will be
   * built. This graph has an additional not accepting starting state.
   *
   * @param noAccepting number of states the automaton can hold in
   * @param noNotAccepting number of states the automaton cannot hold in
   * @param inputWords the words that will be used to learn the automaton. Every word has to be
   *     marked as being part of the language or not
   */
  public AutomataLearning(int noAccepting, int noNotAccepting, Map<List<T>, Boolean> inputWords) {
    automaton = constructAutomaton(noAccepting, noNotAccepting);
    this.inputWords = inputWords;
  }

  private FeedbackAutomaton<T> constructAutomaton(int noAccepting, int noNotAccepting) {
    Collection<ProbabilityState<T>> states = new ArrayList<>(noAccepting + noNotAccepting);
    IntStream.range(0, noAccepting).forEach(__ -> states.add(new ProbabilityState<>(true)));
    IntStream.range(0, noNotAccepting).forEach(__ -> states.add(new ProbabilityState<>(false)));
    ProbabilityState<T> start = new ProbabilityState<>(false);
    Stream.concat(Stream.of(start), states.stream())
        .forEach(state -> state.initTransitionsTo(states));

    states.add(start); // add after init, as start should not be returned to

    return new FeedbackAutomaton<>(states, start);
  }

  private void applyWord(List<T> word, boolean inLanguage) {
    automaton.goToStart();
    word.forEach(automaton::takeLetter);
    if (automaton.canHold() == inLanguage) {
      automaton.positiveFeedback();
    }
  }
}
