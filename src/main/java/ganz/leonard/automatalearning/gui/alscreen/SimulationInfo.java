package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class SimulationInfo<T> extends JPanel implements PropertyChangeListener {

  private final AutomataLearning<T> model;
  private final JLabel appliedWordsNr;
  private final JLabel inputWordsNr;

  public SimulationInfo(AutomataLearning<T> model) {
    this.model = model;
    model.addPropertyChangeListener(this);
    GuiUtil.pad(this);
    setLayout(new GridLayout(2, 2));

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
    update();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(this::update);
  }

  private void update() {
    appliedWordsNr.setText(String.valueOf(model.getNrAppliedWords()));
    inputWordsNr.setText(String.valueOf(model.getNrInputWords()));
  }
}
