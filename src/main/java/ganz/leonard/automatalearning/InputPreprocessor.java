package ganz.leonard.automatalearning;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import ganz.leonard.automatalearning.learning.InputWord;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class InputPreprocessor {

  /**
   * Balance the number of positive (in language) and negative (not in language) input words. The
   * original input stays unmodified. The resulting input list starts with the original input and is
   * appended with cyclically repeated words from the smaller group. Therefore, the resulting list
   * is at least as long as the original list and at most double the size of the larger group.
   *
   * @param unbalanced original input that is potentially unbalanced
   * @return a balanced input list
   * @throws IllegalArgumentException if balancing is not possible because the group for one
   *     polarity is empty
   */
  public static <T> List<InputWord<T>> balancePositiveAndNegative(List<InputWord<T>> unbalanced) {
    List<InputWord<T>> positives = unbalanced.stream().filter(InputWord::inLang).toList();
    List<InputWord<T>> negatives = new ArrayList<>(unbalanced);
    negatives.removeAll(positives);
    if (positives.size() != negatives.size() && (positives.size() == 0 || negatives.size() == 0)) {
      throw new IllegalArgumentException(
          "Cannot balance input if the group for one polarity is empty");
    }
    int difference = Math.abs(positives.size() - negatives.size());
    List<InputWord<T>> smaller = positives.size() < negatives.size() ? positives : negatives;
    Stream<InputWord<T>> additions = Streams.stream(Iterables.cycle(smaller)).limit(difference);
    return Stream.concat(unbalanced.stream(), additions).toList();
  }

  public static <T> List<InputWord<T>> withoutEmptyWord(List<InputWord<T>> inputWords) {
    return inputWords.stream().filter(word -> word.word().size() > 0).toList();
  }

  public static <T> boolean isEmptyWordAccepted(List<InputWord<T>> inputWords) {
    return inputWords.stream().anyMatch(word -> word.word().size() == 0 && word.inLang());
  }
}
