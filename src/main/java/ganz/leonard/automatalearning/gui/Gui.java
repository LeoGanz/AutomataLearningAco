package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.gui.alscreen.AutomataLearningScreen;
import ganz.leonard.automatalearning.gui.optionsscreen.OptionsScreen;
import ganz.leonard.automatalearning.gui.optionsscreen.OptionsScreenModel;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Gui extends JFrame {

  private final GuiController controller;

  public Gui(GuiController controller) {
    this.controller = controller;
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
  }

  public void showOptionsScreen(
      OptionsScreenModel optionsScreenModel) {
    clearAllContent();
    OptionsScreen screen = new OptionsScreen(controller, optionsScreenModel);
    add(screen);
    getRootPane().setDefaultButton(screen.getDefaultButton());
    finishPanelSwitch();
  }

  public <T> void showAutomataLearningScreen(
      AutomataLearning<T> model, RenderManager<T> renderManager) {
    clearAllContent();
    add(new AutomataLearningScreen(controller, model, renderManager));
    finishPanelSwitch();
  }

  private void clearAllContent() {
    getContentPane().removeAll();
  }

  public void makeVisible() {
    setVisible(true);
  }

  private void finishPanelSwitch() {
    setMinimumSize(new Dimension(0, 0));
    pack();
    setLocationRelativeTo(null);
    setMinimumSize(getSize());
  }

  public void displayError(String s) {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(() -> displayError(s));
    } else {
      JOptionPane.showMessageDialog(this, s, "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
