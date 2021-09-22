package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Leaf;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GuiController {

  private Gui gui;
  private AutomataLearning<Character> model;
  private RenderManager<Character> renderManager;

  public void initAndShowGui() {
    gui = new Gui(this);
    gui.showOptionsScreen();
    gui.makeVisible();
  }

  public void simulationScreenRequested(AutomataLearningOptions options) {
    GuiUtil.executeOnSwingWorker(
        () -> {
          Map<List<Character>, Boolean> input = constructDefaultInput(options.inputSamples());
          model = new AutomataLearning<>(options, input);
        },
        () -> {
          renderManager = new RenderManager<>(model);
          gui.showAutomataLearningScreen(model, renderManager);
          renderManager.constructNewFrame();
        });
  }

  private Map<List<Character>, Boolean> constructDefaultInput(int samples) {
    Language<Character> testLang =
        new Language<>(new Leaf<>('a').rep().seq(new Leaf<>('b'))); // a*b
    Map<List<Character>, Boolean> input =
        IntStream.range(0, samples)
            .boxed()
            .collect(Collectors.toMap(__ -> testLang.generateSample(), __ -> true, (k1, k2) -> k1));
    input.put(List.of('a'), false);
    input.put(List.of('b'), true);
    input.put(List.of('a', 'a'), false);
    input.put(List.of('b', 'a'), false);
    input.put(List.of('a', 'b', 'a'), false);
    input.put(List.of('b', 'b', 'a'), false);
    input.put(List.of('a', 'a', 'a'), false);
    input.put(List.of('a', 'a', 'a', 'a'), false);
    input.put(List.of('b', 'a', 'a', 'a'), false);
    input.put(List.of('a', 'a', 'b', 'a'), false);
    input.put(List.of('b', 'b'), false);
    return input;
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
