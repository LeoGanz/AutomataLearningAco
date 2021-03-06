package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.gui.optionsscreen.OptionsScreenModel;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.InputWord;
import ganz.leonard.automatalearning.learning.UpdateImportance;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import javax.swing.SwingUtilities;

public class GuiController {

  private Gui gui;
  private OptionsScreenModel optionsScreenModel;
  private AutomataLearning<Object> model;
  private RenderManager<Object> renderManager;

  public void initAndShowGui() {
    gui = new Gui(this);
    optionsScreenRequested();
    gui.makeVisible();
  }

  public void optionsScreenRequested() {
    GuiUtil.executeOnSwingWorker(
        () -> {
          stopRendering();
          optionsScreenModel = new OptionsScreenModel();
        },
        () -> gui.showOptionsScreen(optionsScreenModel));
  }

  public void simulationScreenRequested(AutomataLearningOptions options) {
    GuiUtil.executeOnSwingWorker(
        () -> {
          List<InputWord<Object>> input = null;
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
            stopRendering();
            renderManager = new RenderManager<>(model);
            gui.showAutomataLearningScreen(model, renderManager);
            renderManager.constructNewFrame(UpdateImportance.HIGH);
          } else {
            gui.displayError(
                "Could not start simulation. Possible causes include empty input files.");
          }
        });
  }

  public void showBestAutomaton(boolean selected) {
    renderManager.setShowBestAutomatonOnly(selected);
  }

  private void stopRendering() {
    if (renderManager != null) {
      renderManager.stop();
    }
  }

  public void requestRepack() {
    SwingUtilities.invokeLater(() -> gui.pack());
  }

  public void nextColonies(int amount) {
    GuiUtil.executeOnSwingWorker(() -> model.runColonies(amount));
  }

  public void requestsMinDfaProbChange(double newProb) {
    GuiUtil.executeOnSwingWorker(() -> model.updateMinDfaProb(newProb));
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

  public void requestedAutomaticColonySize(boolean automaticSize) {
    GuiUtil.executeOnSwingWorker(() -> optionsScreenModel.setAutomaticColonySize(automaticSize));
  }

  public void requestedBalanceInput(boolean balanceInput) {
    GuiUtil.executeOnSwingWorker(() -> optionsScreenModel.setBalanceInput(balanceInput));
  }
}
