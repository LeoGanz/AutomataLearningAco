package ganz.leonard.automatalearning.learning;

import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.ProbabilityState;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AutomataLearning<T> {
  private final FeedbackAutomaton<T> automaton;
  private final Map<List<T>, Boolean> inputWords;
  private final PropertyChangeSupport pcs;
  private Iterator<Map.Entry<List<T>, Boolean>> it;

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
    it = inputWords.entrySet().iterator();
    pcs = new PropertyChangeSupport(this);
  }

  private FeedbackAutomaton<T> constructAutomaton(int noAccepting, int noNotAccepting) {
    Collection<ProbabilityState<T>> states = new ArrayList<>(noAccepting + noNotAccepting);
    AtomicInteger id = new AtomicInteger();
    ProbabilityState<T> start = new ProbabilityState<>(id.getAndIncrement(), false);
    IntStream.range(0, noAccepting)
        .forEach(__ -> states.add(new ProbabilityState<>(id.getAndIncrement(), true)));
    IntStream.range(0, noNotAccepting)
        .forEach(__ -> states.add(new ProbabilityState<>(id.getAndIncrement(), false)));
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
    automaton.decay();
  }

  public boolean hasNextWord() {
    return it.hasNext();
  }

  public void runNextWord() {
    if (!hasNextWord()) {
      throw new NoSuchElementException("No next input word available");
    }

    Map.Entry<List<T>, Boolean> pair = it.next();
    applyWord(pair.getKey(), pair.getValue());
  }

  public void runRemainingWords() {
    while (hasNextWord()) {
      runNextWord();
    }
  }

  public void runWords(int amount) {
    for (int i = 0; i < amount; i++) {
      if (!hasNextWord()) {
        it = inputWords.entrySet().iterator();
      }
      runNextWord();
    }
  }

  public FeedbackAutomaton<T> getAutomaton() {
    return automaton;
  }

  public void addPropertyChangeListener(PropertyChangeListener changeListener) {
    pcs.addPropertyChangeListener(changeListener);
  }
}
