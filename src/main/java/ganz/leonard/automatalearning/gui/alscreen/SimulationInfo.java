package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.Util;
import ganz.leonard.automatalearning.gui.RenderManager;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class SimulationInfo<T> extends JPanel implements PropertyChangeListener {

  public static final int SPACE = 10;
  private final JLabel appliedWordsNr;
  private final JLabel inputWordsNr;

  public SimulationInfo(RenderManager<T> renderManager, AutomataLearning<T> model) {
    renderManager.addPropertyChangeListener(this);
    GuiUtil.pad(this);
    setLayout(new GridLayout(4, 2));

    JLabel appliedWordsInfo = new JLabel("Applied Words: ");
    appliedWordsInfo.setHorizontalAlignment(SwingConstants.RIGHT);
    add(appliedWordsInfo);
    appliedWordsNr = new JLabel();
    add(appliedWordsNr);
    JLabel inputWordsInfo = new JLabel("Unique input words: ");
    inputWordsInfo.setHorizontalAlignment(SwingConstants.RIGHT);
    add(inputWordsInfo);
    inputWordsNr = new JLabel();
    add(inputWordsNr);
    add(Box.createVerticalStrut(SPACE));
    add(Box.createVerticalStrut(SPACE));
    JLabel minRenderProbInfo = new JLabel("Minimum probability to render transition: ");
    minRenderProbInfo.setHorizontalAlignment(SwingConstants.RIGHT);
    add(minRenderProbInfo);
    JLabel minRenderProbValue = new JLabel(
        Util.formatDouble(GraphRenderer.getMinProbToRender(model.getAutomaton()), 2));
    add(minRenderProbValue);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case RenderManager.APPLIED_WORDS_UPDATE_KEY -> SwingUtilities.invokeLater(() ->
          updateNrApplied((int) evt.getNewValue()));

      case RenderManager.INPUT_WORDS_UPDATE_KEY -> SwingUtilities.invokeLater(() ->
          updateNrInput((int) evt.getNewValue()));
      default -> { /* Event not intended for this panel */ }
    }
  }

  private void updateNrApplied(int nrApplied) {
    appliedWordsNr.setText(String.valueOf(nrApplied));
  }

  private void updateNrInput(int nrInput) {
    inputWordsNr.setText(String.valueOf(nrInput));
  }
}
