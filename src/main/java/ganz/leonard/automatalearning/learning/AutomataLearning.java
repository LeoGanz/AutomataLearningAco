package ganz.leonard.automatalearning.learning;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.probability.Ant;
import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.ProbabilityState;
import ganz.leonard.automatalearning.automata.tools.DfaToRegexConverter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AutomataLearning<T> {
  public static final int NR_MEDIUM_IMPORTANCE_UPDATES = 3;
  private final FeedbackAutomaton<T> automaton;
  private final PropertyChangeSupport pcs;
  private final Map<List<T>, Boolean> inputWords;
  private final AutomataLearningOptions options;
  private Iterator<Map.Entry<List<T>, Boolean>> it;
  private int nrAppliedWords = 0;
  private final Set<Ant<T>> antsInCurrentRun;

  private IntermediateResult<T> intermediateDfa;
  private IntermediateResult<T> bestDfa = new IntermediateResult<>(0, null, -1);

  /**
   * Initialize a new automata learning setup. A graph of accepting and not accepting states will be
   * built. This graph has an additional not accepting starting state.
   *
   * @param options collection of all the options used for running automata learning
   * @param inputWords the words that will be used to learn the automaton. Every word has to be
   *     marked as being part of the language or not. At least one word is required.
   * @throws IllegalArgumentException if inputWords is empty
   */
  public AutomataLearning(AutomataLearningOptions options, Map<List<T>, Boolean> inputWords) {
    if (inputWords.isEmpty()) {
      throw new IllegalArgumentException("At least one word has to be provided as input");
    }
    this.options = options;
    it = inputWords.entrySet().iterator();
    automaton = constructAutomaton(options);
    this.inputWords = inputWords;
    antsInCurrentRun = new HashSet<>();
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

  private void createAnt(List<T> word, boolean inLanguage) {
    refillIteratorIfNeeded();
    Ant<T> ant = new Ant<>(word, inLanguage, automaton);
    antsInCurrentRun.add(ant);
    ant.buildSolution();
    nrAppliedWords++;
  }

  private void refillIteratorIfNeeded() {
    if (!it.hasNext()) {
      it = inputWords.entrySet().iterator();
    }
  }

  private synchronized void runSingleColony(UpdateImportance importance) {
    Map.Entry<List<T>, Boolean> pair = it.next();
    int colonySize = options.colonySize() < 1 ? inputWords.size() : options.colonySize();
    for (int i = 0; i < colonySize; i++) {
      createAnt(pair.getKey(), pair.getValue());
    }

    if (getIntermediateResult().score() > bestDfa.score()) {
      bestDfa = intermediateDfa;
    }

    automaton.decay();
    antsInCurrentRun.forEach(Ant::distributePheromones);
    antsInCurrentRun.clear();
    notifyListeners(importance);
  }

  public void runColonies(int amount) {
    // runs up to x updates of medium importance
    // last update with high importance
    int spacingForMedium = Math.max(1, amount / (NR_MEDIUM_IMPORTANCE_UPDATES + 1));
    int nrMediumsSent = 0; // don't exceed max which might happen due to rounding otherwise
    for (int i = 0; i < amount; i++) {
      UpdateImportance importance = UpdateImportance.LOW;
      if ((i + 1) % spacingForMedium == 0 && nrMediumsSent < NR_MEDIUM_IMPORTANCE_UPDATES) {
        nrMediumsSent++;
        importance = UpdateImportance.MEDIUM;
      }
      if (i == (amount - 1)) {
        importance = UpdateImportance.HIGH;
      }
      runSingleColony(importance);
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

  private void notifyListeners(UpdateImportance importance) {
    pcs.firePropertyChange("AutomataLearning", importance, this);
  }

  public IntermediateResult<T> getIntermediateResult() {
    if (intermediateDfa == null || intermediateDfa.nrAppliedWords() != nrAppliedWords) {
      DeterministicFiniteAutomaton<T> dfa = this.automaton.buildMostLikelyDfa();
      intermediateDfa = new IntermediateResult<>(nrAppliedWords, dfa, calcMatchingInputScore(dfa));
    }
    return intermediateDfa;
  }

  public IntermediateResult<T> getBestResult() {
    return bestDfa;
  }

  public String getLanguageRegex() {
    try {
      return DfaToRegexConverter.convert(getIntermediateResult().automaton());
    } catch (IOException | InterruptedException e) {
      // fall through to default return
    }
    return "N/A";
  }

  public double calcMatchingInputScore(DeterministicFiniteAutomaton<T> dfa) {
    double correctlyMatched =
        inputWords.entrySet().stream()
            .map(entry -> entry.getValue().equals(dfa.accepts(entry.getKey())))
            .filter(b -> b)
            .count();
    return correctlyMatched / inputWords.size();
  }
}
