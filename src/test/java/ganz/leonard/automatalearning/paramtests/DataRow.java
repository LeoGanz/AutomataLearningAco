package ganz.leonard.automatalearning.paramtests;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.learning.IntermediateResult;

// can't use records unfortunately as csveed library isn't compatible yet
public class DataRow<T> {
  private int colony;
  private double score;
  private DeterministicFiniteAutomaton<T> dfa;

  public DataRow(int colony, double score, DeterministicFiniteAutomaton<T> dfa) {
    this.colony = colony;
    this.score = score;
    this.dfa = dfa;
  }

  public static <T> DataRow<T> fromIntermediateResult(IntermediateResult<T> data, int colony) {
    return new DataRow<>(colony, data.score(), data.automaton());
  }

  public int getColony() {
    return colony;
  }

  public void setColony(int colony) {
    this.colony = colony;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  public DeterministicFiniteAutomaton<T> getDfa() {
    return dfa;
  }

  public void setDfa(DeterministicFiniteAutomaton<T> dfa) {
    this.dfa = dfa;
  }
}
