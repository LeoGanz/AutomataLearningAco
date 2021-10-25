package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Symbol;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class LanguageTest {

  public static final int NR_SAMPLES = 500;

  public static void main(String[] args) {
    Language<String> testLang =
        new Language<>(new Symbol<>("Fst").rep().seq(new Symbol<>("Snd"))); // a*b
    System.out.println("Language: " + testLang);

    SortedMap<List<?>, Integer> frequencies = new TreeMap<>(Comparator.comparingInt(List::size));
    for (int i = 0; i < NR_SAMPLES; i++) {
      List<String> sample = testLang.generateSample();
      int count = frequencies.getOrDefault(sample, 0);
      frequencies.put(sample, count + 1);
    }

    // visualize distribution of samples
    System.out.println("Occurrences of samples: ");
    int mostDigits = frequencies.lastKey().toString().length();
    frequencies.forEach(
        (list, count) ->
            System.out.println(
                " ".repeat(mostDigits - list.toString().length())
                    + list
                    + "  |  "
                    + "*".repeat(count)));
  }
}
