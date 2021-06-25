package ganz.leonard.al_ants.language;

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
}
