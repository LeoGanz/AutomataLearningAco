package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.gui.GuiController;
import javax.swing.SwingUtilities;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(Main::showGui);
  }

  private static void showGui() {
    new GuiController().initAndShowGui();
  }
}
