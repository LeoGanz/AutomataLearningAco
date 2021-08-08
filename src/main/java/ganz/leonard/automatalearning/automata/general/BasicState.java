package ganz.leonard.automatalearning.automata.general;

public abstract class BasicState<S extends State<S, T>, T> implements State<S, T> {
  private final int id;
  private final boolean isAccepting;

  public BasicState(int id, boolean isAccepting) {
    if (id < 0) {
      throw new IllegalArgumentException("Id must be positive");
    }
    this.id = id;
    this.isAccepting = isAccepting;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public boolean isAccepting() {
    return isAccepting;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BasicState<?, ?> that = (BasicState<?, ?>) o;

    if (id != that.id) {
      return false;
    }
    return isAccepting == that.isAccepting;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (isAccepting ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "St" + id + "(" + (isAccepting ? "+" : "-") + ")";
  }
}
