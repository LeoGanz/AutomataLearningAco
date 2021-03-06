package ganz.leonard.automatalearning.automata.tools;

/**
 * Wrap an object's string representation with a specified string or \" by default.
 * This is mainly used to make interpretation in python scripts easier.
 *
 * @param <T> the object to be wrapped
 */
record ToStringWrapper<T>(T object, String wrappingString) {
  public ToStringWrapper(T object) {
    this(object, "\\\"");
  }

  @Override
  public String toString() {
    return wrappingString + object + wrappingString;
  }
}
