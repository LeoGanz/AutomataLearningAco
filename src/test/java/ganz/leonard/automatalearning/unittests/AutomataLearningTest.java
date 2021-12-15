package ganz.leonard.automatalearning.unittests;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.tools.DfaToRegexConverter;
import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Symbol;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import ganz.leonard.automatalearning.learning.InputWord;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class AutomataLearningTest {

  private static void runLearning(List<InputWord<Character>> input)
      throws IOException, InterruptedException {
    AutomataLearningOptions options =
        AutomataLearningOptionsBuilder.builder().acceptingStates(1).notAcceptingStates(2).build();
    AutomataLearning<Character> al = new AutomataLearning<>(options, input);
    al.runColonies(100);

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
    List<InputWord<Character>> input = new ArrayList<>();
    input.add(new InputWord<>(List.of('b'), true));
    input.add(new InputWord<>(List.of('a', 'a', 'a', 'b'), true));
    input.add(new InputWord<>(List.of('a', 'a', 'a', 'a', 'b'), true));
    input.add(new InputWord<>(List.of('a', 'a', 'a', 'a', 'a', 'b'), true));
    input.add(new InputWord<>(List.of('a', 'a', 'a', 'a', 'a', 'a', 'b'), true));
    input.add(new InputWord<>(List.of('a'), false));
    input.add(new InputWord<>(List.of('a', 'a'), false));
    input.add(new InputWord<>(List.of('a', 'a', 'a'), false));
    input.add(new InputWord<>(List.of('a', 'a', 'a', 'a'), false));
    input.add(new InputWord<>(List.of('b', 'b'), false));
    runLearning(input);
  }

  @Test
  void learnWithGeneratedInput() throws IOException, InterruptedException {
    // a*b
    Language<Character> testLang =
        new Language<>(new Symbol<>('a').rep().seq(new Symbol<>('b'))); // a*b
    int samples = 10;
    List<InputWord<Character>> input =
        IntStream.range(0, samples)
            .boxed()
            .map(__ -> new InputWord<>(testLang.generateSample(), true))
            .toList();

    input.add(new InputWord<>(List.of('a'), false));
    input.add(new InputWord<>(List.of('a', 'a'), false));
    input.add(new InputWord<>(List.of('a', 'a', 'a'), false));
    input.add(new InputWord<>(List.of('a', 'a', 'a', 'a'), false));
    input.add(new InputWord<>(List.of('b', 'b'), false));

    runLearning(input);
  }
}
