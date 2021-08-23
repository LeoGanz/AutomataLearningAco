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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AutomataLearning<T> {
  private final FeedbackAutomaton<T> automaton;
  private final PropertyChangeSupport pcs;
  private final Map<List<T>, Boolean> inputWords;
  private Iterator<Map.Entry<List<T>, Boolean>> it;
  private boolean firstRoundOfWords = true;
  private int nrAppliedWords = 0;

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
        .forEach(state -> state.addTransitionsTo(states));

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
    nrAppliedWords++;
    notifyListeners();
  }

  public boolean hasNextWord() {
    return firstRoundOfWords && it.hasNext();
  }

  private void ensureIteratorHasNext() {
    if (!it.hasNext()) {
      it = inputWords.entrySet().iterator();
      firstRoundOfWords = false;
    }
  }

  public void runNextWord() {
    ensureIteratorHasNext();
    Map.Entry<List<T>, Boolean> pair = it.next();
    applyWord(pair.getKey(), pair.getValue());
  }

  public void runRemainingWords() {
    // only run words of first round
    while (hasNextWord()) {
      runNextWord();
    }
  }

  public void runWords(int amount) {
    for (int i = 0; i < amount; i++) {
      runNextWord();
    }
  }

  public FeedbackAutomaton<T> getAutomaton() {
    return automaton;
  }

  /**
   * Get the current automaton but in an unlinked state so internal changes in automata learning
   * won't be reflected in this automaton. Basically a deep copy.
   *
   * @return an unlinked automaton with the properties described above
   */
  public FeedbackAutomaton<T> getUnlinkedAutomaton() {
    System.out.println("creating deep copy of current automaton");
    return FeedbackAutomaton.copyFeedbackAutomaton(automaton);
  }

  public int getNrAppliedWords() {
    return nrAppliedWords;
  }

  public int getNrInputWords() {
    return inputWords.size();
  }

  public void addPropertyChangeListener(PropertyChangeListener changeListener) {
    pcs.addPropertyChangeListener(changeListener);
  }

  private void notifyListeners() {
    pcs.firePropertyChange("AutomataLearning", null, this);
  }
}
