package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class GraphComponent<T> extends JPanel implements PropertyChangeListener {

  private static final int IMAGE_HEIGHT = 500;
  private final AutomataLearning<T> model;
  private BufferedImage img;

  public GraphComponent(AutomataLearning<T> model) {
    this.model = model;
    model.addPropertyChangeListener(this);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (img != null) {
      g.drawImage(img, 0, 0, this);
    }
  }

  @Override
  public Dimension getPreferredSize() {
    if (img != null) {
      return new Dimension(img.getWidth(), img.getHeight());
    } else {
      return super.getPreferredSize();
    }
  }

  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  @Override
  public Dimension getMaximumSize() {
    // without providing a maximum, the panel becomes larger than the image so the image is not
    // centered anymore
    return getPreferredSize();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(this::update);
  }

  private void update() {
    img = GraphRenderer.automatonToImg(model.getAutomaton(), IMAGE_HEIGHT);
    invalidate();
    repaint();
  }
}
