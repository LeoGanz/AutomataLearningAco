package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import net.miginfocom.swing.MigLayout;

public class OptionsScreen extends JPanel {

  public static final float TITLE_FONT_SIZE = 25f;
  private final JButton go;

  public OptionsScreen(GuiController controller) {
    setLayout(new MigLayout("insets 30", "grow", "10"));

    JLabel title = new JLabel("Options for Automata Learning");
    title.setFont(title.getFont().deriveFont(TITLE_FONT_SIZE));
    add(title, "span, center, wrap 20");

    SpinnerNumberModel acceptingStates =
        new SpinnerNumberModel(AutomataLearningOptions.DEF_ACCEPTING_STATES, 1, 100, 1);
    addOption("Accepting States:", acceptingStates, true, false);
    SpinnerNumberModel notAcceptingStates =
        new SpinnerNumberModel(AutomataLearningOptions.DEF_NOT_ACCEPTING_STATES, 0, 100, 1);
    addOption("Not accepting States:", notAcceptingStates, false, true);
    add(new JSeparator(), "span, growx, wrap");

    SpinnerNumberModel initialPheromones =
        new SpinnerNumberModel(AutomataLearningOptions.DEF_INITIAL_PHEROMONES, 0.000001, 100, 1);
    addOption("Initial Pheromones:", initialPheromones, true, false);
    SpinnerNumberModel positiveFeedback =
        new SpinnerNumberModel(AutomataLearningOptions.DEF_POSITIVE_FEEDBACK_FACTOR, 1, 100, 0.1);
    addOption("Positive Feedback Factor:", positiveFeedback, false, false);
    SpinnerNumberModel negativeFeedback =
        new SpinnerNumberModel(
            AutomataLearningOptions.DEF_NEGATIVE_FEEDBACK_FACTOR, 0.000001, 1, 0.1);
    addOption("Negative Feedback Factor:", negativeFeedback, false, true);
    add(new JSeparator(), "span, growx, wrap");

    SpinnerNumberModel samples =
        new SpinnerNumberModel(AutomataLearningOptions.DEF_INPUT_SAMPLES, 1, 1000, 1);
    addOption("Input Samples to generate:", samples, true, true, true);

    JButton reset = new JButton("Reset to Defaults");
    reset.addActionListener(
        e -> {
          acceptingStates.setValue(AutomataLearningOptions.DEF_ACCEPTING_STATES);
          notAcceptingStates.setValue(AutomataLearningOptions.DEF_NOT_ACCEPTING_STATES);
          initialPheromones.setValue(AutomataLearningOptions.DEF_INITIAL_PHEROMONES);
          positiveFeedback.setValue(AutomataLearningOptions.DEF_POSITIVE_FEEDBACK_FACTOR);
          negativeFeedback.setValue(AutomataLearningOptions.DEF_NEGATIVE_FEEDBACK_FACTOR);
          samples.setValue(AutomataLearningOptions.DEF_INPUT_SAMPLES);
        });
    go = new JButton("Start Automata Learning");
    go.addActionListener(
        e ->
            controller.simulationScreenRequested(
                AutomataLearningOptionsBuilder.builder()
                    .acceptingStates(acceptingStates.getNumber().intValue())
                    .notAcceptingStates(notAcceptingStates.getNumber().intValue())
                    .initialPheromones(initialPheromones.getNumber().intValue())
                    .positiveFeedbackFactor(positiveFeedback.getNumber().doubleValue())
                    .negativeFeedbackFactor(negativeFeedback.getNumber().doubleValue())
                    .inputSamples(samples.getNumber().intValue())
                    .build()));

    add(go, "tag ok, span, split 2, sizegroup button");
    add(reset, "tag cancel, sizegroup button");
  }

  public JButton getDefaultButton() {
    return go;
  }

  private void addOption(
      String name, SpinnerNumberModel numberModel, boolean firstInLine, boolean wrapAfter) {
    addOption(name, numberModel, firstInLine, wrapAfter, false);
  }

  private void addOption(
      String name,
      SpinnerNumberModel numberModel,
      boolean firstInLine,
      boolean wrapAfter,
      boolean pushWrap) {
    add(new JLabel(name), "align label" + (firstInLine ? "" : ", gap unrelated"));

    StringBuilder constraints = new StringBuilder("sizegroup spinner");
    if (wrapAfter || pushWrap) {
      constraints.append(", wrap");
    }
    if (pushWrap) {
      constraints.append(" 30:push");
    }
    add(new JSpinner(numberModel), constraints.toString());
  }
}
