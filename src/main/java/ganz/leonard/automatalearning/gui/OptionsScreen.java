package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.gui.util.GuiUtil;
import javax.swing.JButton;
import javax.swing.JPanel;

public class OptionsScreen extends JPanel {

  public OptionsScreen(GuiController controller) {
    GuiUtil.pad(this);
    JButton useDefaultSettings = new JButton("Use default settings");
    useDefaultSettings.addActionListener(e -> controller.simulationScreenRequested());
    add(GuiUtil.centerHorizontally(useDefaultSettings));
    useDefaultSettings.requestFocusInWindow();
  }
}
