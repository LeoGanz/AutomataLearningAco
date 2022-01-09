package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Symbol;
import ganz.leonard.automatalearning.learning.InputWord;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InputProvider {

  public static final char SYMBOL_IN_LANG = '+';
  public static final char SYMBOL_NOT_IN_LANG = '-';
  private static final String INPUT_FILE_DIRECTORY = "ganz/leonard/automatalearning/input/";
  private static final Map<Language<?>, List<InputWord<Object>>> LANGUAGES_WITH_DEFAULTS =
      new HashMap<>();

  static {
    // a*b
    List<InputWord<Object>> input = new ArrayList<>();
    input.add(new InputWord<>(List.of('a'), false));
    input.add(new InputWord<>(List.of('b'), true));
    input.add(new InputWord<>(List.of('a', 'a'), false));
    input.add(new InputWord<>(List.of('b', 'a'), false));
    input.add(new InputWord<>(List.of('a', 'b', 'a'), false));
    input.add(new InputWord<>(List.of('b', 'b', 'a'), false));
    input.add(new InputWord<>(List.of('a', 'a', 'a'), false));
    input.add(new InputWord<>(List.of('a', 'a', 'a', 'a'), false));
    input.add(new InputWord<>(List.of('b', 'a', 'a', 'a'), false));
    input.add(new InputWord<>(List.of('a', 'a', 'b', 'a'), false));
    input.add(new InputWord<>(List.of('b', 'b'), false));
    Language<Character> asThenB = new Language<>(new Symbol<>('a').rep().seq(new Symbol<>('b')));
    LANGUAGES_WITH_DEFAULTS.put(asThenB, input);

    // aa*b
    input = new ArrayList<>(input);
    input.remove(new InputWord<>(new ArrayList<Object>(List.of('b')), true));
    input.add(new InputWord<>(List.of('b'), false)); // only difference to samples above
    Language<Character> leadingaThenAsThenB =
        new Language<>(new Symbol<>('a').seq(new Symbol<>('a').rep()).seq(new Symbol<>('b')));
    LANGUAGES_WITH_DEFAULTS.put(leadingaThenAsThenB, input);

    // 12*3

    Language<Integer> oneTwosThree =
        new Language<>(new Symbol<>(1).seq(new Symbol<>(2).rep()).seq(new Symbol<>(3)));
    LANGUAGES_WITH_DEFAULTS.put(oneTwosThree, List.of());
  }

  public static Set<Path> getAvailableInputFiles() {
    try (Stream<Path> fileStream =
        Files.list(Paths.get(ClassLoader.getSystemResource(INPUT_FILE_DIRECTORY).toURI()))) {
      return fileStream.filter(file -> !Files.isDirectory(file)).collect(Collectors.toSet());
    } catch (IOException | URISyntaxException e) {
      return Set.of();
    }
  }

  public static Set<Language<?>> getAvailableGeneratingLanguages() {
    return LANGUAGES_WITH_DEFAULTS.keySet();
  }

  /**
   * Expected file format (in no particular order):<br>
   * <br>
   * +wordInLanguage<br>
   * +nextWordInLang<br>
   * -notInLang<br>
   * ...
   *
   * @return input words and whether they're part of the language or not
   * @throws IOException if the specified file cannot be read
   */
  public static List<InputWord<Object>> readFromFile(Path pathInResources) throws IOException {
    System.out.println("Reading input from: " + pathInResources.toAbsolutePath());
    List<String> lines = Files.readAllLines(pathInResources);
    return lines.stream()
        .filter(
            line ->
                line != null
                    && line.length() >= 1
                    && (line.charAt(0) == SYMBOL_IN_LANG || line.charAt(0) == SYMBOL_NOT_IN_LANG))
        .map(
            line ->
                new InputWord<>(
                    new ArrayList<Object>(
                        line.chars()
                            .mapToObj(ch -> (char) ch)
                            .skip(1)
                            .collect(Collectors.toList())),
                    line.charAt(0) == SYMBOL_IN_LANG))
        .toList();
  }

  /**
   * Generate sample input that is in the language and add some default samples if language is
   * known.
   *
   * @param language language for which to generate samples
   * @param samples number of samples to generate - non-unique samples will be purged
   * @return input words and whether they're part of the language or not. Due to duplicates being
   *     removed and defaults being added the final amount of samples is in general not equal to the
   *     parameter 'samples'
   */
  public static List<InputWord<Object>> generateSamples(Language<?> language, int samples) {
    List<InputWord<Object>> input =
        IntStream.range(0, samples)
            .boxed()
            .map(__ -> new InputWord<Object>(new ArrayList<>(language.generateSample()), true))
            .collect(Collectors.toList());
    input.addAll(LANGUAGES_WITH_DEFAULTS.get(language));
    return input;
  }
}
