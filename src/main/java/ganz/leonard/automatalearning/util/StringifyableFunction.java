package ganz.leonard.automatalearning.util;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class StringifyableFunction<T, R> implements Function<T, R> {
  public abstract String stringRep();

  @Override
  public String toString() {
    return stringRep();
  }

  public StringifyableFunction<T, R> wrapWithPredicate(
      Predicate<R> predicate, Supplier<RuntimeException> testFailed) {
    return new StringifyableFunction<>() {
      @Override
      public String stringRep() {
        return StringifyableFunction.this.stringRep();
      }

      @Override
      public R apply(T t) {
        R val = StringifyableFunction.this.apply(t);
        if (!predicate.test(val)) {
          throw testFailed.get();
        }
        return val;
      }
    };
  }
}
