package ganz.leonard.automatalearning.automata.tools;

import ganz.leonard.automatalearning.Util;
import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import ganz.leonard.automatalearning.automata.general.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GreeneryNotationConverter<T> {

  private Map<Integer, T> letterSaver;

  public List<String> toGreeneryNotation(
      DeterministicFiniteAutomaton<T> dfa, boolean useHashcodes) {
    List<String> result = new ArrayList<>(5);
    final Set<T> alphabet =
        dfa.getAllStates().values().stream()
            .flatMap(detState -> detState.getOutgoingTransitions().keySet().stream())
            .collect(Collectors.toSet());

    if (useHashcodes) {
      // used to translate script output back to actual objects; toString may not be unique, hashes
      // hopefully are basically all the time
      letterSaver =
          alphabet.stream().collect(Collectors.toMap(Objects::hashCode, Function.identity()));
    }
    final Set<Integer> states = dfa.getAllStates().keySet();
    int initial = dfa.getStartState().getId();
    final Set<Integer> finals =
        dfa.getAllStates().values().stream()
            .filter(DeterministicState::isAccepting)
            .map(DeterministicState::getId)
            .collect(Collectors.toSet());
    final Map<Integer, Map<Object, Integer>> transitions =
        dfa.getAllStates().values().stream()
            .collect(
                Collectors.toMap(
                    DeterministicState::getId,
                    detState ->
                        detState.getOutgoingTransitions().entrySet().stream()
                            .collect(
                                Collectors.toMap(
                                    entry ->
                                        useHashcodes
                                            ? entry.getKey().hashCode()
                                            : new ToStringWrapper<>(entry.getKey()),
                                    entry -> entry.getValue().getId()))));

    if (useHashcodes) {
      result.add(letterSaver.keySet().toString());
    } else {
      Set<ToStringWrapper<T>> wrappedAlphabet =
          alphabet.stream().map(ToStringWrapper::new).collect(Collectors.toSet());
      result.add(wrappedAlphabet.toString());
    }
    result.add(states.toString());
    result.add(String.valueOf(initial));
    result.add(finals.toString());
    result.add(transitions.toString().replace('=', ':'));
    return result;
  }

  public DeterministicFiniteAutomaton<T> fromGreeneryNotation(String line) {
    int finalsIndexStart = Util.calcSubstringStart("finals = {", line);
    int finalsIndexEnd = line.indexOf('}', finalsIndexStart);
    List<String> finals =
        Arrays.asList(line.substring(finalsIndexStart, finalsIndexEnd).split(", "));

    int statesIndexStart = Util.calcSubstringStart("states = {", line);
    int statesIndexEnd = line.indexOf('}', statesIndexStart);
    String[] states = line.substring(statesIndexStart, statesIndexEnd).split(", ");
    Map<Integer, DeterministicState<T>> allStates =
        Stream.of(states)
            .map(
                stateStr ->
                    new DeterministicState<T>(
                        Integer.parseInt(stateStr), finals.contains(stateStr)))
            .collect(Collectors.toMap(State::getId, Function.identity()));

    int initialIndexStart = Util.calcSubstringStart("initial = ", line);
    int initialIndexEnd = line.indexOf(',', initialIndexStart);
    int startId = Integer.parseInt(line.substring(initialIndexStart, initialIndexEnd));
    DeterministicState<T> start = allStates.get(startId);

    int mapIndexStart = Util.calcSubstringStart("map = {", line);
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
}
