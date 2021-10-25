package ganz.leonard.automatalearning.gui.optionsscreen;

import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Symbol;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InputProvider {

  private static final String INPUT_FILE_DIRECTORY = "ganz/leonard/automatalearning/input/";
  private static final Map<Language<?>, Map<List<Object>, Boolean>> LANGUAGES_WITH_DEFAULTS =
      new HashMap<>();

  static {
    // a*b
    Map<List<Object>, Boolean> input = new HashMap<>();
    input.put(List.of('a'), false);
    input.put(List.of('b'), true);
    input.put(List.of('a', 'a'), false);
    input.put(List.of('b', 'a'), false);
    input.put(List.of('a', 'b', 'a'), false);
    input.put(List.of('b', 'b', 'a'), false);
    input.put(List.of('a', 'a', 'a'), false);
    input.put(List.of('a', 'a', 'a', 'a'), false);
    input.put(List.of('b', 'a', 'a', 'a'), false);
    input.put(List.of('a', 'a', 'b', 'a'), false);
    input.put(List.of('b', 'b'), false);
    Language<Character> asThenB = new Language<>(new Symbol<>('a').rep().seq(new Symbol<>('b')));
    LANGUAGES_WITH_DEFAULTS.put(asThenB, input);

    // aa*b
    input.put(List.of('b'), false); // only difference to samples above
    Language<Character> leadingaThenAsThenB =
        new Language<>(new Symbol<>('a').seq(new Symbol<>('a').rep()).seq(new Symbol<>('b')));
    LANGUAGES_WITH_DEFAULTS.put(leadingaThenAsThenB, input);

    // 12*3

    Language<Integer> oneTwosThree =
        new Language<>(new Symbol<>(1).seq(new Symbol<>(2).rep()).seq(new Symbol<>(3)));
    LANGUAGES_WITH_DEFAULTS.put(oneTwosThree, Map.of());
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
  public static Map<List<Object>, Boolean> readFromFile(Path pathInResources) throws IOException {
    System.out.println("Reading input from: " + pathInResources.toAbsolutePath());
    List<String> lines = Files.readAllLines(pathInResources);
    return lines.stream()
        .filter(
            line ->
                line != null
                    && line.length() >= 1
                    && (line.charAt(0) == '+' || line.charAt(0) == '-'))
        .collect(
            Collectors.toMap(
                line -> line.chars().mapToObj(ch -> (char) ch).skip(1).collect(Collectors.toList()),
                line -> line.charAt(0) == '+'));
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
  public static Map<List<Object>, Boolean> generateSamples(Language<?> language, int samples) {
    Map<List<Object>, Boolean> input =
        IntStream.range(0, samples)
            .boxed()
            .collect(
                Collectors.toMap(
                    __ -> (List<Object>) language.generateSample(), __ -> true, (k1, k2) -> k1));
    input.putAll(LANGUAGES_WITH_DEFAULTS.get(language));
    return input;
  }
}
