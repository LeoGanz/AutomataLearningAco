package ganz.leonard.automatalearning.gui.alscreen;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;

import ganz.leonard.automatalearning.Util;
import ganz.leonard.automatalearning.automata.general.Automaton;
import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.State;
import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.PheromoneTransition;
import ganz.leonard.automatalearning.automata.probability.ProbabilityState;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphRenderer {
  public static final int TRANS_TO_DRAW_PER_LETTER_AND_STATE = 2;
  private static final int MAX_LINE_WIDTH = 5;
  private static final String HEX_BLACK = "000000";
  private static final String HEX_RED = "FF0000";
  private static final int RGBA_MAX_VAL = 255;
  private static final double MIN_PORTION_OF_RANDOM_CHOICE_PROB = 1.0 / 3;

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
                    .forEach(
                        (letter, target) -> buildLink(nodes, state, target, letter, 1, false)));
    return constructGraph(nodes, "dfa", height);
  }

  public static <T> BufferedImage automatonToImg(FeedbackAutomaton<T> automaton, int height) {
    DeterministicFiniteAutomaton<T> dfa = automaton.buildMostLikelyDfa();

    Map<Integer, Node> nodes = constructNodes(automaton);
    double minProbToRender = getMinProbToRender(automaton);
    automaton
        .getAllStates()
        .values()
        .forEach(state -> constructTransitions(dfa, state, nodes, minProbToRender));
    return constructGraph(nodes, "feedback", height);
  }

  private static <T> void constructTransitions(
      DeterministicFiniteAutomaton<T> dfa,
      ProbabilityState<T> fromState,
      Map<Integer, Node> nodes,
      double minProbToRender) {
    fromState
        .getUsedLetters()
        .forEach(
            letter -> {
              Map<ProbabilityState<T>, Double> probabilities =
                  fromState.collectTransitionProbabilities(letter);
              fromState.getOutgoingTransitions().entrySet().stream()
                  .sorted(
                      Comparator.comparingDouble(
                              (Map.Entry<ProbabilityState<T>, PheromoneTransition<T>>
                                      targetTrans) ->
                                  targetTrans.getValue().getPheromoneFor(letter))
                          .reversed())
                  .limit(TRANS_TO_DRAW_PER_LETTER_AND_STATE)
                  .filter(
                      targetTrans ->
                          probabilities.get(targetTrans.getKey())
                              > (minProbToRender - Util.DOUBLE_COMPARISON_PRECISION))
                  .forEach(
                      targetTrans ->
                          buildLink(
                              nodes,
                              fromState,
                              targetTrans.getKey(),
                              letter,
                              probabilities.get(targetTrans.getKey()),
                              dfa.hasTransition(
                                  fromState.getId(), targetTrans.getKey().getId(), letter)));
            });
  }

  public static <T> double getMinProbToRender(FeedbackAutomaton<T> automaton) {
    // normal nodes cannot return to start
    int possibleTransitions = automaton.getAllStates().size() - 1;
    double prob = 1.0 / possibleTransitions;
    // if prob drops below 1/k of the prob for choosing randomly don't render (to keep graph clean)
    prob *= MIN_PORTION_OF_RANDOM_CHOICE_PROB;
    return prob;
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
    nodes.put(
        -1,
        startNode.link(
            to(nodes.get(automaton.getStartState().getId()))
                .with(Color.rgba(HEX_RED + Integer.toHexString(RGBA_MAX_VAL)))));
    return nodes;
  }

  private static <S extends State<S, T>, T> void buildLink(
      Map<Integer, Node> nodes,
      State<S, T> source,
      State<S, T> target,
      T letter,
      double probability,
      boolean highlight) {
    nodes.put(
        source.getId(),
        nodes
            .get(source.getId())
            .link(
                to(nodes.get(target.getId()))
                    .with(
                        Label.of(String.valueOf(letter)),
                        Style.lineWidth(probability * MAX_LINE_WIDTH),
                        // problems with rgba method with 4 values
                        Color.rgba(
                            (highlight ? HEX_RED : HEX_BLACK)
                                + Integer.toHexString((int) (RGBA_MAX_VAL * probability))))));
  }

  private static BufferedImage constructGraph(Map<Integer, Node> nodes, String name, int height) {
    Graph graph = graph(name).directed().with(nodes.values().stream().toList());
    return Graphviz.fromGraph(graph).height(height).render(Format.PNG).toImage();
  }
}
