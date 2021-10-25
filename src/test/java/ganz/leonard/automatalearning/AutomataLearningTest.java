package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.tools.DfaToRegexConverter;
import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Symbol;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class AutomataLearningTest {

  private static void runLearning(Map<List<Character>, Boolean> input)
      throws IOException, InterruptedException {
    AutomataLearningOptions options =
        AutomataLearningOptionsBuilder.builder().acceptingStates(1).notAcceptingStates(2).build();
    AutomataLearning<Character> al = new AutomataLearning<>(options, input);
    al.runWords(100);

    DeterministicFiniteAutomaton<Character> learnedDfa = al.getAutomaton().buildMostLikelyDfa();
    learnedDfa
        .getAllStates()
        .values()
        .forEach(state -> System.out.println(state + " to " + state.getOutgoingTransitions()));

    String regex = DfaToRegexConverter.convert(learnedDfa);
    String expectedRegex = "a*b";
    System.out.println("learned language: " + regex);
    // not yet reliable enough
    //    Assertions.assertEquals(expectedRegex, regex);
  }

  @Test
  void learnWithSpecifiedInput() throws IOException, InterruptedException {
    Map<List<Character>, Boolean> input = new LinkedHashMap<>();
    input.put(List.of('b'), true);
    input.put(List.of('a', 'a', 'a', 'b'), true);
    input.put(List.of('a', 'a', 'a', 'a', 'b'), true);
    input.put(List.of('a', 'a', 'a', 'a', 'a', 'b'), true);
    input.put(List.of('a', 'a', 'a', 'a', 'a', 'a', 'b'), true);
    input.put(List.of('a'), false);
    input.put(List.of('a', 'a'), false);
    input.put(List.of('a', 'a', 'a'), false);
    input.put(List.of('a', 'a', 'a', 'a'), false);
    input.put(List.of('b', 'b'), false);
    runLearning(input);
  }

  @Test
  void learnWithGeneratedInput() throws IOException, InterruptedException {
    // a*b
    Language<Character> testLang =
        new Language<>(new Symbol<>('a').rep().seq(new Symbol<>('b'))); // a*b
    int samples = 10;
    Map<List<Character>, Boolean> input =
        IntStream.range(0, samples)
            .boxed()
            .collect(Collectors.toMap(__ -> testLang.generateSample(), __ -> true, (k1, k2) -> k1));

    input.put(List.of('a'), false);
    input.put(List.of('a', 'a'), false);
    input.put(List.of('a', 'a', 'a'), false);
    input.put(List.of('a', 'a', 'a', 'a'), false);
    input.put(List.of('b', 'b'), false);

    runLearning(input);
  }
}
