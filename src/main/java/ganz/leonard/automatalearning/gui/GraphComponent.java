package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;

class GraphComponent<T> extends JComponent implements PropertyChangeListener {

  private final AutomataLearning<T> model;

  public GraphComponent(AutomataLearning<T> model) {
    model.addPropertyChangeListener(this);
    this.model = model;

    setPreferredSize(new Dimension(800, 400));
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    FeedbackAutomaton<T> automaton = model.getAutomaton();

    // For testing TODO remove test code
    //    DeterministicState<Character> fst = new DeterministicState<>(1, false);
    //    DeterministicState<Character> snd = new DeterministicState<>(2, true);
    //    fst.addTransitions(Map.of('a', fst, 'b', snd));
    //    DeterministicFiniteAutomaton<Character>
    //        dfa = new DeterministicFiniteAutomaton<>(Set.of(fst, snd), fst);

    BufferedImage img = GraphRenderer.automatonToImg(automaton);
    g.drawImage(img, 0, 0, this);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // trigger repaint, new data will be drawn from the existing model reference
    repaint();
  }
}
