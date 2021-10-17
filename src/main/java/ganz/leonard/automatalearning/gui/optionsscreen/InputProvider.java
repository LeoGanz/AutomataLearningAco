package ganz.leonard.automatalearning.gui.optionsscreen;

import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Leaf;
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
  private static final Map<Language<Character>, Map<List<Character>, Boolean>>
      LANGUAGES_WITH_DEFAULTS = new HashMap<>();

  static {
    Map<List<Character>, Boolean> input = new HashMap<>();
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
    Language<Character> asThenB = new Language<>(new Leaf<>('a').rep().seq(new Leaf<>('b')));
    LANGUAGES_WITH_DEFAULTS.put(asThenB, input);
  }

  public static Set<Path> getAvailableInputFiles() {
    try (Stream<Path> fileStream =
        Files.list(Paths.get(ClassLoader.getSystemResource(INPUT_FILE_DIRECTORY).toURI()))) {
      return fileStream.filter(file -> !Files.isDirectory(file)).collect(Collectors.toSet());
    } catch (IOException | URISyntaxException e) {
      return Set.of();
    }
  }

  public static Set<Language<Character>> getAvailableGeneratingLanguages() {
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
  public static Map<List<Character>, Boolean> readFromFile(Path pathInResources)
      throws IOException {
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
  public static Map<List<Character>, Boolean> generateSamples(
      Language<Character> language, int samples) {
    Map<List<Character>, Boolean> input =
        IntStream.range(0, samples)
            .boxed()
            .collect(Collectors.toMap(__ -> language.generateSample(), __ -> true, (k1, k2) -> k1));
    input.putAll(LANGUAGES_WITH_DEFAULTS.get(language));
    return input;
  }
}