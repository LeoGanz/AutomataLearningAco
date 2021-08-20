package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.gui.util.AncestorAdapter;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;

class SimulationControls<T> extends JPanel implements PropertyChangeListener {

  private static final int WORDS_AMOUNT = 10;
  private final AutomataLearning<T> model;
  private final JButton remaining;

  public SimulationControls(GuiController controller, AutomataLearning<T> model) {
    this.model = model;
    model.addPropertyChangeListener(this);
    GuiUtil.pad(this);

    JLabel runWords = new JLabel("Run simulation with: ");
    add(runWords);
    JButton next = new JButton("Next Word");
    next.addActionListener(e -> controller.nextWord());
    add(next);
    JButton nextWords = new JButton("Next " + WORDS_AMOUNT + " Words");
    nextWords.addActionListener(e -> controller.nextWords(WORDS_AMOUNT));
    add(nextWords);
    remaining = new JButton("Remaining Words");
    remaining.addActionListener(e -> controller.remainingWords());
    add(remaining);
    update();
    addAncestorListener(
        new AncestorAdapter() {
          @Override
          public void ancestorAdded(AncestorEvent event) {
            next.requestFocusInWindow();
          }
        });
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(this::update);
  }

  private void update() {
    remaining.setEnabled(model.hasNextWord());
    // only disable remaining, as next and next X can use words multiple times
  }
}
