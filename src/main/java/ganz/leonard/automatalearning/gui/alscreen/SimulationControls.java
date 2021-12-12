package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.gui.RenderManager;
import ganz.leonard.automatalearning.gui.util.AncestorAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import net.miginfocom.swing.MigLayout;

class SimulationControls<T> extends JPanel implements PropertyChangeListener {

  private static final int PADDING = 15;
  private static final int FEW_COLONIES_AMOUNT = 10;
  private static final int MANY_COLONIES_AMOUNT = 100;
  private final RenderManager<T> renderManager;
  private final JCheckBox showBest;

  public SimulationControls(GuiController controller, RenderManager<T> renderManager) {
    this.renderManager = renderManager;
    renderManager.addPropertyChangeListener(this);
    setLayout(new MigLayout("insets " + PADDING));

    add(Box.createHorizontalGlue(), "pushx");
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
    add(manyColonies, "pushx");
    JButton backToOptions = new JButton("Back");
    backToOptions.addActionListener(e -> controller.optionsScreenRequested());
    add(backToOptions, "wrap");

    showBest = new JCheckBox("Show best automaton so far", renderManager.isShowBestAutomatonOnly());
    showBest.addActionListener(e -> controller.showBestAutomaton(showBest.isSelected()));
    add(showBest, "span, center");

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
    if (RenderManager.SHOW_BEST_UPDATE_KEY.equals(evt.getPropertyName())) {
      showBest.setSelected((boolean) evt.getNewValue());
    }
  }
}
