package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

public class AutomataLearningScreen extends JPanel implements PropertyChangeListener {
  public <T> AutomataLearningScreen(GuiController controller, AutomataLearning<T> model) {
    model.addPropertyChangeListener(this);

    add(new GraphComponent<>(model));
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {}
}
