package ganz.leonard.automatalearning.automata.tools;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import ganz.leonard.automatalearning.automata.general.State;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Minimizer<T> {

  // I know that implementing a common minimization algorithm would most likely be a lot easier but
  // let's instead have some fun with python :)

  private Map<Integer, T> letterSaver;

  public DeterministicFiniteAutomaton<T> minimize(DeterministicFiniteAutomaton<T> dfa)
      throws IOException, InterruptedException {
    String baseCmd = ".gradle/python/Scripts/python.exe";
    // fix only working on windows
    String scriptPath = "src/main/python/ganz/leonard/automatalearning/automata_tools/minimize.py";
    // fix path (src/main/python should not be needed)
    List<String> cmd = new ArrayList<>();
    cmd.add(baseCmd);
    cmd.add(scriptPath);
    cmd.addAll(toGreeneryNotation(dfa));
    ProcessBuilder pb = new ProcessBuilder(cmd);
    pb.redirectErrorStream(true);
    Process p = pb.start();
    p.waitFor();

    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
    DeterministicFiniteAutomaton<T> minimizedDfa = null;
    while (in.ready()) {
      String line = in.readLine();
      System.out.println(line);
      if (line.startsWith("fsm")) {
        minimizedDfa = fromGreeneryNotation(line);
      }
    }

    return minimizedDfa;
  }

  private List<String> toGreeneryNotation(DeterministicFiniteAutomaton<T> dfa) {
    List<String> result = new ArrayList<>(5);
    Set<T> alphabet =
        dfa.getAllStates().values().stream()
            .flatMap(detState -> detState.getOutgoingTransitions().keySet().stream())
            .collect(Collectors.toSet());
    // used to translate script output back to actual objects; toString may not be unique, hashes
    // hopefully are basically all the time
    letterSaver =
        alphabet.stream().collect(Collectors.toMap(Objects::hashCode, Function.identity()));
    Set<Integer> states = dfa.getAllStates().keySet();
    int initial = dfa.getStartState().getId();
    Set<Integer> finals =
        dfa.getAllStates().values().stream()
            .filter(DeterministicState::isAccepting)
            .map(DeterministicState::getId)
            .collect(Collectors.toSet());
    Map<Integer, Map<Integer, Integer>> transitions =
        dfa.getAllStates().values().stream()
            .collect(
                Collectors.toMap(
                    DeterministicState::getId,
                    detState ->
                        detState.getOutgoingTransitions().entrySet().stream()
                            .collect(
                                Collectors.toMap(
                                    entry -> entry.getKey().hashCode(),
                                    entry -> entry.getValue().getId()))));

    result.add(letterSaver.keySet().toString());
    result.add(states.toString());
    result.add(String.valueOf(initial));
    result.add(finals.toString());
    result.add(transitions.toString().replace('=', ':'));
    return result;
  }

  private DeterministicFiniteAutomaton<T> fromGreeneryNotation(String line) {
    int finalsIndexStart = calcSubstringStart("finals = {", line);
    int finalsIndexEnd = line.indexOf('}', finalsIndexStart);
    List<String> finals =
        Arrays.asList(line.substring(finalsIndexStart, finalsIndexEnd).split(", "));

    int statesIndexStart = calcSubstringStart("states = {", line);
    int statesIndexEnd = line.indexOf('}', statesIndexStart);
    String[] states = line.substring(statesIndexStart, statesIndexEnd).split(", ");
    Map<Integer, DeterministicState<T>> allStates =
        Stream.of(states)
            .map(
                stateStr ->
                    new DeterministicState<T>(
                        Integer.parseInt(stateStr), finals.contains(stateStr)))
            .collect(Collectors.toMap(State::getId, Function.identity()));

    int initialIndexStart = calcSubstringStart("initial = ", line);
    int initialIndexEnd = line.indexOf(',', initialIndexStart);
    int startId = Integer.parseInt(line.substring(initialIndexStart, initialIndexEnd));
    DeterministicState<T> start = allStates.get(startId);

    int mapIndexStart = calcSubstringStart("map = {", line);
    int mapIndexEnd = line.lastIndexOf('}');
    String map = line.substring(mapIndexStart, mapIndexEnd);
    String[] mapEntries = map.split("}, ");
    Map<Integer, Map<T, DeterministicState<T>>> transitions =
        Arrays.stream(mapEntries)
            .map(entry -> entry.split(": \\{", 2))
            .collect(
                Collectors.toMap(
                    arr -> Integer.parseInt(arr[0]),
                    arr -> parseTransitionsForSingleState(arr[1], allStates)));

    transitions.forEach(
        (stateId, transForState) -> allStates.get(stateId).addTransitions(transForState));
    return new DeterministicFiniteAutomaton<>(allStates, start);
  }

  private Map<T, DeterministicState<T>> parseTransitionsForSingleState(
      String s, Map<Integer, DeterministicState<T>> allStates) {
    return Arrays.stream(s.split(", "))
        .filter(entry -> entry.length() >= 4)
        .map(entry -> entry.split(": ", 2))
        .collect(
            Collectors.toMap(
                arr -> letterSaver.get(Integer.parseInt(arr[0])),
                arr -> allStates.get(Integer.parseInt(arr[1]))));
  }

  private int calcSubstringStart(String key, String line) {
    return line.indexOf(key) + key.length();
  }
}
