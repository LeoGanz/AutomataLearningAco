package ganz.leonard.automatalearning.learning;

import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.PheromoneTransition;
import java.util.List;
import javafx.util.Pair;

public class Ant<T> {
  private final boolean inLanguage;
  private final FeedbackAutomaton<T> automaton;
  private final List<T> word;
  private List<Pair<PheromoneTransition<T>, T>> solutionPath;
  private boolean solutionCorrect;

  public Ant(List<T> word, boolean inLanguage, FeedbackAutomaton<T> automaton) {
    this.word = word;
    this.inLanguage = inLanguage;
    this.automaton = automaton;
  }

  public void buildSolution() {
    automaton.goToStart();
    // possibility to add online step-by-step pheromone update (after each step)
    // ignoring the result from takeLetter is not neat, but does not matter as feedback automatons
    // always have transitions for all letters.
    word.forEach(automaton::takeLetter);
    solutionPath = automaton.getCurrentPath();
    solutionCorrect = automaton.canHold() == inLanguage;
  }

  public void distributePheromones() {
    if (solutionPath == null) {
      throw new IllegalStateException(
          "Ant has to construct solution before distributing pheromones");
    }
    // apply online delayed pheromone update
    automaton.feedback(solutionPath, solutionCorrect);
  }
}
