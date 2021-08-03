package ganz.leonard.automatalearning.automata.general;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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
    if (!outgoingTransitions.containsKey(letter)) {
      throw new NoSuchElementException("No Transition known for this letter");
    }
    return outgoingTransitions.get(letter);
  }
}
