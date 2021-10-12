package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class GuiController {

  private Gui gui;
  private AutomataLearning<Character> model;
  private RenderManager<Character> renderManager;

  public void initAndShowGui() {
    gui = new Gui(this);
    gui.showOptionsScreen();
    gui.makeVisible();
  }

  public void simulationScreenRequested(AutomataLearningOptions options, boolean useFile) {
    GuiUtil.executeOnSwingWorker(
        () -> {
          Map<List<Character>, Boolean> input;
          if (useFile) {
            try {
              input = InputProvider.readFromFile(InputProvider.INPUT_FILE_LOCATION);
              System.out.println(input);
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            } catch (URISyntaxException e) {
              throw new RuntimeException(e);
            }
          } else {
            input = InputProvider.generateSamples(options.inputSamples());
          }
          model = new AutomataLearning<>(options, input);
        },
        () -> {
          renderManager = new RenderManager<>(model);
          gui.showAutomataLearningScreen(model, renderManager);
          renderManager.constructNewFrame();
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
}
