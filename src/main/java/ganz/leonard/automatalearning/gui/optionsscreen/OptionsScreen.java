package ganz.leonard.automatalearning.gui.optionsscreen;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;

public class OptionsScreen extends JPanel implements PropertyChangeListener {

  public static final float TITLE_FONT_SIZE = 25f;
  private final JButton go;
  private final OptionsScreenModel optionsScreenModel;
  private final JCheckBox generateSamples;
  private final JCheckBox automaticColonySize;
  private final List<JComponent> componentsOfGenerate;
  private final List<JComponent> componentsOfFiles;
  private final List<JComponent> componentsOfColony;
  private final JComboBox<Language<Character>> availableGeneratingLanguages;
  private final JComboBox<PathWrapper> availableInputFiles;

  public OptionsScreen(GuiController controller, OptionsScreenModel optionsScreenModel) {
    this.optionsScreenModel = optionsScreenModel;
    optionsScreenModel.addPropertyChangeListener(this);
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
    addSeparator();

    SpinnerNumberModel initialPheromones =
        new SpinnerNumberModel(AutomataLearningOptions.DEF_INITIAL_PHEROMONES, 0, 100, 1);
    addOption("Initial Pheromones:", initialPheromones, true, false);
    SpinnerNumberModel feedback =
        new SpinnerNumberModel(AutomataLearningOptions.DEF_FEEDBACK, 0, 100, 0.1);
    addOption("Feedback:", feedback, false, false);
    SpinnerNumberModel decay =
        new SpinnerNumberModel(AutomataLearningOptions.DEF_DECAY_FACTOR, 0.000001, 1, 0.1);
    addOption("Decay Factor:", decay, false, true);
    addSeparator();

    automaticColonySize =
        new JCheckBox("Colony Size = No. input words", optionsScreenModel.isAutomaticColonySize());
    automaticColonySize.addActionListener(
        e -> controller.requestedAutomaticColonySize(automaticColonySize.isSelected()));
    add(automaticColonySize, "span 2");
    SpinnerNumberModel colonySize =
        new SpinnerNumberModel(AutomataLearningOptions.DEF_COLONY_SIZE, 1, 10000, 1);
    componentsOfColony = addOption("Colony Size:", colonySize, false, true);
    addSeparator();

    generateSamples = new JCheckBox("Generate Input Words", optionsScreenModel.isGenerateSamples());
    generateSamples.addActionListener(
        e -> controller.requestedGenerateSamples(generateSamples.isSelected()));
    add(generateSamples, "span 2");
    availableGeneratingLanguages =
        new JComboBox<>(
            optionsScreenModel
                .getAvailableGeneratingLanguages()
                .toArray((IntFunction<Language<Character>[]>) Language[]::new));
    availableGeneratingLanguages.addItemListener(
        e -> controller.requestedSelectedGeneratingLanguage((Language<Character>) e.getItem()));
    componentsOfGenerate =
        // arraylist needed to make result mutable
        new ArrayList<>(
            addOption("for language:", availableGeneratingLanguages, false, 0, false, false));
    SpinnerNumberModel samples =
        new SpinnerNumberModel(AutomataLearningOptions.DEF_INPUT_SAMPLES, 1, 1000, 1);
    componentsOfGenerate.addAll(addOption("Samples to generate:", samples, false, true, false));

    availableInputFiles =
        new JComboBox<>(
            optionsScreenModel.getAvailableInputFiles().stream()
                .map(PathWrapper::new)
                .toArray(PathWrapper[]::new));
    availableInputFiles.addItemListener(
        e -> controller.requestedSelectedInputFile(((PathWrapper) e.getItem()).path()));
    componentsOfFiles =
        addOption("instead use input file:", availableInputFiles, false, 2, true, true);
    updateInputComponents();

    JButton reset = new JButton("Reset to Defaults");
    reset.addActionListener(
        e -> {
          acceptingStates.setValue(AutomataLearningOptions.DEF_ACCEPTING_STATES);
          notAcceptingStates.setValue(AutomataLearningOptions.DEF_NOT_ACCEPTING_STATES);
          initialPheromones.setValue(AutomataLearningOptions.DEF_INITIAL_PHEROMONES);
          feedback.setValue(AutomataLearningOptions.DEF_FEEDBACK);
          decay.setValue(AutomataLearningOptions.DEF_DECAY_FACTOR);
          samples.setValue(AutomataLearningOptions.DEF_INPUT_SAMPLES);
          colonySize.setValue(AutomataLearningOptions.DEF_COLONY_SIZE);
          optionsScreenModel.resetToDefaults();
        });
    go = new JButton("Start Automata Learning");
    go.addActionListener(
        e ->
            controller.simulationScreenRequested(
                AutomataLearningOptionsBuilder.builder()
                    .acceptingStates(acceptingStates.getNumber().intValue())
                    .notAcceptingStates(notAcceptingStates.getNumber().intValue())
                    .initialPheromones(initialPheromones.getNumber().intValue())
                    .feedback(feedback.getNumber().doubleValue())
                    .decayFactor(decay.getNumber().doubleValue())
                    .inputSamples(samples.getNumber().intValue())
                    .colonySize(
                        optionsScreenModel.isAutomaticColonySize()
                            ? -1
                            : colonySize.getNumber().intValue())
                    .build()));

    add(go, "tag ok, span, split 2, sizegroup button");
    add(reset, "tag cancel, sizegroup button");
  }

  private void addSeparator() {
    add(new JSeparator(), "span, growx, wrap");
  }

  private void updateInputComponents() {
    generateSamples.setSelected(optionsScreenModel.isGenerateSamples());
    automaticColonySize.setSelected(optionsScreenModel.isAutomaticColonySize());
    availableGeneratingLanguages.setSelectedItem(
        optionsScreenModel.getSelectedGeneratingLanguage());
    availableInputFiles.setSelectedItem(new PathWrapper(optionsScreenModel.getSelectedInputFile()));
    componentsOfGenerate.forEach(comp -> comp.setEnabled(optionsScreenModel.isGenerateSamples()));
    componentsOfFiles.forEach(comp -> comp.setEnabled(!optionsScreenModel.isGenerateSamples()));
    componentsOfColony.forEach(
        comp -> comp.setEnabled(!optionsScreenModel.isAutomaticColonySize()));
  }

  public JButton getDefaultButton() {
    return go;
  }

  private List<JComponent> addOption(
      String name, SpinnerNumberModel numberModel, boolean firstInLine, boolean wrapAfter) {
    return addOption(name, numberModel, firstInLine, wrapAfter, false);
  }

  private List<JComponent> addOption(
      String name,
      SpinnerNumberModel numberModel,
      boolean firstInLine,
      boolean wrapAfter,
      boolean pushWrap) {
    return addOption(name, new JSpinner(numberModel), firstInLine, 0, wrapAfter, pushWrap);
  }

  private List<JComponent> addOption(
      String name,
      JComponent chooser,
      boolean firstInLine,
      int skipCellsBefore,
      boolean wrapAfter,
      boolean pushWrap) {
    JLabel label = new JLabel(name);
    add(label, "align label, skip " + skipCellsBefore + (firstInLine ? "" : ", gap unrelated"));

    StringBuilder constraints = new StringBuilder("sizegroup choosingElement");
    if (wrapAfter || pushWrap) {
      constraints.append(", wrap");
    }
    if (pushWrap) {
      constraints.append(" 30:push");
    }

    add(chooser, constraints.toString());
    return List.of(label, chooser);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(this::updateInputComponents);
  }
}
