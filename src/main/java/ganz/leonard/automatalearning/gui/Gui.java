package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Gui extends JFrame {
  private static final int MINIMUM_FRAME_WIDTH = 500;
  private static final int MINIMUM_FRAME_HEIGHT = 500;

  private final GuiController controller;

  public Gui(GuiController controller) {
    this.controller = controller;
    setMinimumSize(new Dimension(MINIMUM_FRAME_WIDTH, MINIMUM_FRAME_HEIGHT));
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    showOptionsScreen();
  }

  private void showOptionsScreen() {
    clearAllContent();
    add(new OptionsScreen(controller));
    pack();
  }

  private void clearAllContent() {
    getContentPane().removeAll();
  }

  public void makeVisible() {
    setVisible(true);
  }

  public <T> void showAutomataLearningScreen(AutomataLearning<T> model) {
    clearAllContent();
    add(new AutomataLearningScreen(controller, model));
    pack();
  }
}
