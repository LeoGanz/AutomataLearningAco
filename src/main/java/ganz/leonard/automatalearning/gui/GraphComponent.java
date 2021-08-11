package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;

class GraphComponent<T> extends JComponent implements PropertyChangeListener {

  private final AutomataLearning<T> model;

  public GraphComponent(AutomataLearning<T> model) {
    model.addPropertyChangeListener(this);
    this.model = model;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    FeedbackAutomaton<T> automaton = model.getAutomaton();
    // paint with graphviz
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // trigger repaint, new data will be drawn from the existing model reference
    repaint();
  }
}
