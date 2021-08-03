package ganz.leonard.automatalearning.language;

import java.util.List;

public interface Expression<T> {

  default Expression<T> seq(Expression<T> snd) {
    return new Sequence<>(this, snd);
  }

  default Expression<T> alt(Expression<T> snd) {
    return new Alternative<>(this, snd);
  }

  default Expression<T> rep() {
    return new Repetition<>(this);
  }

  /**
   * Let a visitor visit the node by executing the overloaded methods of the Visitor.
   *
   * @param visitor the concrete visitor that is visiting the node
   * @return a word over the alphabet <code>T</code>
   */
  List<T> accept(ExpressionVisitor<T> visitor);
}
