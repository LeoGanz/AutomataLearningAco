package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Leaf;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class AutomataLearningTest {
  public static void main(String[] args) {
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
  void learnBasic() {
    // a*b
    Language<Character> testLang =
        new Language<>(new Leaf<>('a').rep().seq(new Leaf<>('b'))); // a*b
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

  private static void runLearning(Map<List<Character>, Boolean> input) {
    AutomataLearning<Character> al = new AutomataLearning<>(1, 2, input);
    al.runWords(100);

    DeterministicFiniteAutomaton<Character> learnedDfa = al.getAutomaton().buildMostLikelyDfa();
    learnedDfa
        .getAllStates()
        .values()
        .forEach(state -> System.out.println(state + " to " + state.getOutgoingTransitions()));
  }
}
