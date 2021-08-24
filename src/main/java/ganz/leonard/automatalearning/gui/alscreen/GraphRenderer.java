package ganz.leonard.automatalearning.gui.alscreen;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;

import ganz.leonard.automatalearning.Util;
import ganz.leonard.automatalearning.automata.general.Automaton;
import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.State;
import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.ProbabilityState;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphRenderer {

  // paint with graphviz

  static {
    Graphviz.useEngine(new GraphvizV8Engine());
  }

  public static <T> BufferedImage automatonToImg(
      DeterministicFiniteAutomaton<T> automaton, int height) {
    Map<Integer, Node> nodes = constructNodes(automaton);
    automaton
        .getAllStates()
        .values()
        .forEach(
            state ->
                state
                    .getOutgoingTransitions()
                    .forEach((letter, target) -> buildLink(nodes, state, target, letter)));
    return constructGraph(nodes, "dfa", height);
  }

  public static <T> BufferedImage automatonToImg(FeedbackAutomaton<T> automaton, int height) {
    Map<Integer, Node> nodes = constructNodes(automaton);
    double minProbToRender = getMinProbToRender(automaton);
    automaton
        .getAllStates()
        .values()
        .forEach(
            state -> {
              Map<T, Map<ProbabilityState<T>, Double>> probabilities =
                  state.getUsedLetters().stream()
                      .collect(
                          Collectors.toMap(
                              Function.identity(), state::getNormalizedTransitionProbabilities));
              state
                  .getOutgoingTransitions()
                  .forEach(
                      (target, transition) ->
                          transition.getKnownLetters().stream()
                              .filter(
                                  letter ->
                                      probabilities.get(letter).get(target)
                                          > (minProbToRender - Util.DOUBLE_COMPARISON_PRECISION))
                              .forEach(letter -> buildLink(nodes, state, target, letter)));
            });
    return constructGraph(nodes, "feedback", height);
  }

  public static <T> double getMinProbToRender(FeedbackAutomaton<T> automaton) {
    // normal nodes cannot return to start and by design not to themselves
    // so the number of possible transitions is n-2
    return 1.0 / (automaton.getAllStates().size() - 2);
  }

  private static <S extends State<S, T>, T> Map<Integer, Node> constructNodes(
      Automaton<S, T> automaton) {
    Map<Integer, Node> nodes =
        automaton.getAllStates().values().stream()
            .collect(
                Collectors.toMap(
                    State::getId,
                    state ->
                        node(String.valueOf(state.getId()))
                            .with(state.isAccepting() ? Shape.DOUBLE_CIRCLE : Shape.CIRCLE)));
    Node startNode = node("start").with(Style.INVIS);
    nodes.put(-1, startNode.link(nodes.get(automaton.getStartState().getId())));
    return nodes;
  }

  private static <S extends State<S, T>, T> void buildLink(
      Map<Integer, Node> nodes, State<S, T> source, State<S, T> target, T letter) {
    nodes.put(
        source.getId(),
        nodes
            .get(source.getId())
            .link(to(nodes.get(target.getId())).with(Label.of(String.valueOf(letter)))));
  }

  private static BufferedImage constructGraph(Map<Integer, Node> nodes, String name, int height) {
    Graph graph = graph(name).directed().with(nodes.values().stream().toList());
    return Graphviz.fromGraph(graph).height(height).render(Format.PNG).toImage();
  }
}
