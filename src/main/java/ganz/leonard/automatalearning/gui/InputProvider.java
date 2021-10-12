package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Leaf;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InputProvider {

  public static final String INPUT_FILE_LOCATION = "ganz/leonard/automatalearning/input/aStarB.txt";

  /**
   * Expected file format (in no particular order):<br>
   * <br>
   * +wordInLanguage<br>
   * +nextWordInLang<br>
   * -notInLang<br>
   *
   * @return input words and whether they're part of the language or not
   * @throws IOException if the specified file cannot be read
   */
  public static Map<List<Character>, Boolean> readFromFile(String pathInResources)
      throws IOException, URISyntaxException {
    Path path = Paths.get(ClassLoader.getSystemResource(pathInResources).toURI());
    System.out.println("Reading input from: " + path.toAbsolutePath());
    List<String> lines = Files.readAllLines(path);
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
   * Generate sample input that is in the language and add some default negative samples.
   *
   * @param samples number of samples to generate - non-unique samples will be purged
   * @return input words and whether they're part of the language or not. Due to duplicates being
   *     removed and default negatives being added the final amount of samples is in general not
   *     equal to the parameter 'samples'
   */
  public static Map<List<Character>, Boolean> generateSamples(int samples) {
    Language<Character> testLang =
        new Language<>(new Leaf<>('a').rep().seq(new Leaf<>('b'))); // a*b
    Map<List<Character>, Boolean> input =
        IntStream.range(0, samples)
            .boxed()
            .collect(Collectors.toMap(__ -> testLang.generateSample(), __ -> true, (k1, k2) -> k1));
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
    return input;
  }
}
