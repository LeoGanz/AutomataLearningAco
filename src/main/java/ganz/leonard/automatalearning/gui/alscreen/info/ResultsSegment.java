package ganz.leonard.automatalearning.gui.alscreen.info;

import ganz.leonard.automatalearning.util.Util;
import ganz.leonard.automatalearning.gui.RenderManager;
import ganz.leonard.automatalearning.gui.alscreen.AutomataLearningScreen;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ResultsSegment<T> extends JPanel implements PropertyChangeListener {

  private final JLabel languageLabel;
  private final JLabel scoreLabel;

  public ResultsSegment(RenderManager<T> renderManager) {
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
    scoreLabel = new JLabel(" ");
    scoreLabel.setFont(scoreLabel.getFont().deriveFont(AutomataLearningScreen.HEADING_FONT_SIZE));
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
