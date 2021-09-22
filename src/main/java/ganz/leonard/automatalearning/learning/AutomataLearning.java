package ganz.leonard.automatalearning.learning;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.ProbabilityState;
import ganz.leonard.automatalearning.automata.tools.DfaToRegexConverter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class AutomataLearning<T> {
  private final FeedbackAutomaton<T> automaton;
  private final PropertyChangeSupport pcs;
  private final Map<List<T>, Boolean> inputWords;
  private Iterator<Map.Entry<List<T>, Boolean>> it;
  private boolean firstRoundOfWords = true;
  private int nrAppliedWords = 0;

  // Save intermediate dfa, specify at which applied word count it was derived
  private Pair<Integer, DeterministicFiniteAutomaton<T>> intermediateDfa;

  /**
   * Initialize a new automata learning setup. A graph of accepting and not accepting states will be
   * built. This graph has an additional not accepting starting state.
   *
   * @param options collection of all the options used for running automata learning
   * @param inputWords the words that will be used to learn the automaton. Every word has to be
   *     marked as being part of the language or not
   */
  public AutomataLearning(AutomataLearningOptions options, Map<List<T>, Boolean> inputWords) {
    automaton = constructAutomaton(options);
    this.inputWords = inputWords;
    it = inputWords.entrySet().iterator();
    pcs = new PropertyChangeSupport(this);
  }

  private FeedbackAutomaton<T> constructAutomaton(AutomataLearningOptions options) {
    Collection<ProbabilityState<T>> states =
        new ArrayList<>(options.notAcceptingStates() + options.notAcceptingStates());
    AtomicInteger id = new AtomicInteger();
    ProbabilityState<T> start = new ProbabilityState<>(id.getAndIncrement(), false, options);
    IntStream.range(0, options.acceptingStates())
        .forEach(__ -> states.add(new ProbabilityState<>(id.getAndIncrement(), true, options)));
    IntStream.range(0, options.notAcceptingStates())
        .forEach(__ -> states.add(new ProbabilityState<>(id.getAndIncrement(), false, options)));
    Stream.concat(Stream.of(start), states.stream())
        .forEach(state -> state.addTransitionsTo(states));

    states.add(start); // add after init, as start should not be returned to

    return new FeedbackAutomaton<>(states, start, options);
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

  public synchronized void runNextWord() {
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
    System.out.println("model processing " + amount + " words");
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

  private DeterministicFiniteAutomaton<T> getIntermediateDfa() {
    if (intermediateDfa == null || !intermediateDfa.getLeft().equals(nrAppliedWords)) {
      intermediateDfa = new ImmutablePair<>(nrAppliedWords, automaton.buildMostLikelyDfa());
    }
    return intermediateDfa.getRight();
  }

  public String getLanguageRegex() {
    try {
      return DfaToRegexConverter.convert(getIntermediateDfa());
    } catch (IOException | InterruptedException e) {
      // fall through to default return
    }
    return "N/A";
  }

  public double getMatchingInputScore() {
    DeterministicFiniteAutomaton<T> dfa = getIntermediateDfa();
    long correctlyMatched =
        inputWords.entrySet().stream()
            .map(entry -> entry.getValue().equals(dfa.accepts(entry.getKey())))
            .filter(b -> b)
            .count();
    return ((double) correctlyMatched) / inputWords.size();
  }
}
