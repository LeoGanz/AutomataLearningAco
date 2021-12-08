package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.gui.RenderManager;
import ganz.leonard.automatalearning.gui.alscreen.graph.GraphComponent;
import ganz.leonard.automatalearning.gui.alscreen.info.ResultsSegment;
import ganz.leonard.automatalearning.gui.alscreen.info.SimulationInfo;
import ganz.leonard.automatalearning.gui.alscreen.inputwords.InputDisplay;
import ganz.leonard.automatalearning.gui.alscreen.legend.ProbabilityControl;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class AutomataLearningScreen extends JPanel {

  public static final float HEADING_FONT_SIZE = 17;

  public <T> AutomataLearningScreen(
      GuiController controller, AutomataLearning<T> model, RenderManager<T> renderManager) {
    setLayout(new BorderLayout());

    JComponent north = Box.createVerticalBox();
    north.add(new SimulationControls(controller));
    north.add(new JSeparator());
    add(north, BorderLayout.NORTH);

    add(GuiUtil.pad(GuiUtil.center(new GraphComponent<>(renderManager))), BorderLayout.CENTER);

    JComponent south = Box.createVerticalBox();
    south.add(new JSeparator());
    south.add(new SimulationInfo<>(renderManager));
    south.add(new ResultsSegment<>(renderManager));
    add(south, BorderLayout.SOUTH);

    JComponent west = Box.createHorizontalBox();
    west.add(new InputDisplay<>(controller, renderManager, model));
    west.add(new JSeparator(SwingConstants.VERTICAL));
    add(west, BorderLayout.WEST);

    JComponent east = Box.createHorizontalBox();
    east.add(new JSeparator(SwingConstants.VERTICAL));
    east.add(new ProbabilityControl(controller, renderManager));
    add(east, BorderLayout.EAST);
  }
}
