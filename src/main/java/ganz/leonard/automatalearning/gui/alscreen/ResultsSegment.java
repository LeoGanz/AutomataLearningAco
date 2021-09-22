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

    update();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals(RenderManager.QUEUE_DONE_KEY)) {
      update();
    }
  }

  private void update() {
    String language = model.getLanguageRegex();
    double score = model.getMatchingInputScore();
    SwingUtilities.invokeLater(() -> updateTexts(language, score));
  }

  private void updateTexts(String regex, double score) {
    languageLabel.setText(regex);
    scoreLabel.setText(Util.formatPercentage(score, 1));
  }
}
