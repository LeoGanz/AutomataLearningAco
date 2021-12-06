package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.Util;
import ganz.leonard.automatalearning.gui.RenderManager;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ResultsSegment<T> extends JPanel implements PropertyChangeListener {

  private final AutomataLearning<T> model;
  private final JLabel languageLabel;
  private final JLabel scoreLabel;

  public ResultsSegment(RenderManager<T> renderManager, AutomataLearning<T> model) {
    this.model = model;
    renderManager.addPropertyChangeListener(this);

    GuiUtil.pad(this);
    setLayout(new GridLayout(2, 2));

    JLabel languageInfo = new JLabel("Learned language: ");
    languageInfo.setHorizontalAlignment(SwingConstants.RIGHT);
    add(languageInfo);
    languageLabel = new JLabel();
    add(languageLabel);

    JLabel scoreInfo = new JLabel("Correctly matched input words: ");
    scoreInfo.setHorizontalAlignment(SwingConstants.RIGHT);
    add(scoreInfo);
    scoreLabel = new JLabel();
    add(scoreLabel);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case RenderManager.REGEX_UPDATE_KEY -> SwingUtilities.invokeLater(() ->
          updateRegex((String) evt.getNewValue()));
      case RenderManager.SCORE_UPDATE_KEY -> SwingUtilities.invokeLater(() ->
          updateScore((double) evt.getNewValue()));
      case RenderManager.REBUILD_GUI_KEY -> SwingUtilities.invokeLater(this::repaint);
      default -> { /* Event not intended for this panel */ }
    }
  }

  private void updateRegex(String newValue) {
    languageLabel.setText(newValue);
  }

  private void updateScore(double newValue) {
    String newScoreText = Double.isNaN(newValue) ? "N/A" : Util.formatPercentage(newValue, 1);
    scoreLabel.setText(newScoreText);
  }
}
