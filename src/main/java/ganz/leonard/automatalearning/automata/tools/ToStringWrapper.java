package ganz.leonard.automatalearning.automata.tools;

/**
 * Wrap an object's string representation with \".
 * This is mainly used to make interpretation in python scripts easier.
 *
 * @param <T> the object to be wrapped
 */
record ToStringWrapper<T>(T object) {
  @Override
  public String toString() {
    return "\\\"" + object + "\\\"";
  }
}
