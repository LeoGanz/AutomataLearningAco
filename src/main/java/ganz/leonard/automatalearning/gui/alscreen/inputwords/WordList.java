package ganz.leonard.automatalearning.gui.alscreen.inputwords;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.gui.RenderManager;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class WordList<T> extends JPanel implements PropertyChangeListener {

  private static final int INDENT = 15;
  private static final int LINE_HEIGHT = 20;
  private final GuiController controller;
  private final AutomataLearning<T> automataLearning;
  private Map.Entry<List<T>, Boolean> nextWord;
  private int minWidth;
  private int minHeight;

  public WordList(
      GuiController controller,
      RenderManager<T> renderManager,
      AutomataLearning<T> automataLearning) {
    this.controller = controller;
    this.automataLearning = automataLearning;
    renderManager.addPropertyChangeListener(this);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    int y = g.getFontMetrics().getHeight();
    for (Map.Entry<List<T>, Boolean> elem : automataLearning.getInput()) {
      if (elem.equals(nextWord)) {
        g2.drawString("\u27A4", 0, y); // arrow
      }
      String word = stringifyEntry(elem);
      g2.drawString(word, INDENT, y);
      int currentWidth = g.getFontMetrics().stringWidth(word) + INDENT;
      if (currentWidth > minWidth) {
        minWidth = currentWidth;
        invalidate();
        controller.requestRepack();
      }
      y += LINE_HEIGHT;
    }

    if (y > minHeight) {
      minHeight = y;
      invalidate();
      controller.requestRepack();
    }
  }

  private String stringifyEntry(Map.Entry<List<T>, Boolean> elem) {
    StringBuilder sb = new StringBuilder(elem.getValue() ? "+" : "-");
    elem.getKey().forEach(sb::append);
    return sb.toString();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(minWidth, minHeight);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (RenderManager.NEXT_WORD_UPDATE_KEY.equals(evt.getPropertyName())) {
      nextWord = (Map.Entry<List<T>, Boolean>) evt.getNewValue();
      SwingUtilities.invokeLater(this::repaint);
    }
  }
}
