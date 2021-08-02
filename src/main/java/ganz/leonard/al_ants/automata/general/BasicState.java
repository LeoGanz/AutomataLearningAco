package ganz.leonard.al_ants.automata.general;

public abstract class BasicState<S extends State<S, T>, T> implements State<S, T> {
  private final boolean isAccepting;

  public BasicState(boolean isAccepting) {
    this.isAccepting = isAccepting;
  }

  @Override
  public boolean isAccepting() {
    return isAccepting;
  }
}
