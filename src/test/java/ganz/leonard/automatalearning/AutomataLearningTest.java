package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AutomataLearningTest {
  public static void main(String[] args) {
    Map<List<Character>, Boolean> input = new LinkedHashMap<>();
    input.put(List.of('a', 'a', 'a', 'a'), true);
    input.put(List.of('a'), true);
    input.put(List.of('a', 'a'), true);
    input.put(List.of('b', 'b'), false);
    input.put(List.of('b'), false);
    AutomataLearning<Character> al = new AutomataLearning<>(2, 2, input);

    al.runRemainingWords();
  }
}
