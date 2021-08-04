package ganz.leonard.automatalearning.automata.general;

import java.util.HashMap;
import java.util.Map;

public class DeterministicState<T> extends BasicState<DeterministicState<T>, T> {

  private final Map<T, DeterministicState<T>> outgoingTransitions;

  public DeterministicState(boolean isAccepting) {
    super(isAccepting);
    outgoingTransitions = new HashMap<>();
  }

  public void initTransitions(Map<T, DeterministicState<T>> outgoingTransitions) {
    this.outgoingTransitions.putAll(outgoingTransitions);
  }

  @Override
  public DeterministicState<T> transit(T letter) {
    return outgoingTransitions.get(letter);
  }
}
