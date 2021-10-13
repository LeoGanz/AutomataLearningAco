package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.gui.optionsscreen.OptionsScreenModel;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class GuiController {

  private Gui gui;
  private OptionsScreenModel optionsScreenModel;
  private AutomataLearning<Character> model;
  private RenderManager<Character> renderManager;

  public void initAndShowGui() {
    gui = new Gui(this);
    optionsScreenRequested();
    gui.makeVisible();
  }

  private void optionsScreenRequested() {
    GuiUtil.executeOnSwingWorker(
        () -> optionsScreenModel = new OptionsScreenModel(),
        () -> gui.showOptionsScreen(optionsScreenModel));
  }

  public void simulationScreenRequested(AutomataLearningOptions options) {
    GuiUtil.executeOnSwingWorker(
        () -> {
          Map<List<Character>, Boolean> input = null;
          try {
            input = optionsScreenModel.getChosenInput(options.inputSamples());
          } catch (IOException | URISyntaxException e) {
            // ignore. simulation will not start and warning will be displayed as model will be null
          }
          if (input != null) {
            model = new AutomataLearning<>(options, input);
          }
        },
        () -> {
          if (model != null) {
            renderManager = new RenderManager<>(model);
            gui.showAutomataLearningScreen(model, renderManager);
            renderManager.constructNewFrame();
          } else {
            gui.displayError(
                "Could not start simulation. Possible causes include empty input files.");
          }
        });
  }

  public void nextWord() {
    GuiUtil.executeOnSwingWorker(() -> model.runNextWord());
  }

  public void nextWords(int amount) {
    GuiUtil.executeOnSwingWorker(() -> model.runWords(amount));
  }

  public void remainingWords() {
    GuiUtil.executeOnSwingWorker(() -> model.runRemainingWords());
  }

  // Delegate requests to options model

  public void requestedSelectedInputFile(Path selectedInputFile) {
    GuiUtil.executeOnSwingWorker(() -> optionsScreenModel.setSelectedInputFile(selectedInputFile));
  }

  public void requestedSelectedGeneratingLanguage(Language<Character> selectedGeneratingLanguage) {
    GuiUtil.executeOnSwingWorker(
        () -> optionsScreenModel.setSelectedGeneratingLanguage(selectedGeneratingLanguage));
  }

  public void requestedGenerateSamples(boolean generateSamples) {
    GuiUtil.executeOnSwingWorker(() -> optionsScreenModel.setGenerateSamples(generateSamples));
  }
}
