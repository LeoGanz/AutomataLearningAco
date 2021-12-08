package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.gui.util.AncestorAdapter;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;

class SimulationControls extends JPanel {

  private static final int FEW_COLONIES_AMOUNT = 10;
  private static final int MANY_COLONIES_AMOUNT = 100;

  public SimulationControls(GuiController controller) {
    GuiUtil.pad(this);

    JLabel runColonies = new JLabel("Run simulation with: ");
    runColonies.setFont(runColonies.getFont().deriveFont(AutomataLearningScreen.HEADING_FONT_SIZE));
    add(runColonies);
    JButton next = new JButton("Next Colony");
    next.addActionListener(e -> controller.nextColonies(1));
    add(next);
    JButton fewColonies = new JButton(FEW_COLONIES_AMOUNT + " Colonies");
    fewColonies.addActionListener(e -> controller.nextColonies(FEW_COLONIES_AMOUNT));
    add(fewColonies);
    JButton manyColonies = new JButton(MANY_COLONIES_AMOUNT + " Colonies");
    manyColonies.addActionListener(e -> controller.nextColonies(MANY_COLONIES_AMOUNT));
    add(manyColonies);
    add(Box.createHorizontalStrut(50));
    JButton backToOptions = new JButton("Back");
    backToOptions.addActionListener(e -> controller.optionsScreenRequested());
    add(backToOptions);
    addAncestorListener(
        new AncestorAdapter() {
          @Override
          public void ancestorAdded(AncestorEvent event) {
            next.requestFocusInWindow();
          }
        });
  }
}
