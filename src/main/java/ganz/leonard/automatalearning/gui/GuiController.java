package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Leaf;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GuiController {
  private static final int FRAMES_TO_KEEP = 3;

  private Gui gui;
  private AutomataLearning<Character> model;
  private RenderManager<Character> renderManager;

  public void initAndShowGui() {
    gui = new Gui(this);
    gui.makeVisible();
  }

  public void simulationScreenRequested() {
    GuiUtil.executeOnSwingWorker(
        () -> {
          // TODO don't hard code but let the user decide
          Map<List<Character>, Boolean> input = constructDefaultInput();
          model = new AutomataLearning<>(2, 2, input);
        },
        () -> {
          renderManager = new RenderManager<>(model);
          gui.showAutomataLearningScreen(model, renderManager);
          renderManager.constructNewFrame();
        });
  }

  private Map<List<Character>, Boolean> constructDefaultInput() {
    Language<Character> testLang =
        new Language<>(new Leaf<>('a').rep().seq(new Leaf<>('b'))); // a*b
    int samples = 10;
    Map<List<Character>, Boolean> input =
        IntStream.range(0, samples)
            .boxed()
            .collect(Collectors.toMap(__ -> testLang.generateSample(), __ -> true, (k1, k2) -> k1));
    input.put(List.of('a'), false);
    input.put(List.of('a', 'a'), false);
    input.put(List.of('a', 'a', 'a'), false);
    input.put(List.of('a', 'a', 'a', 'a'), false);
    input.put(List.of('b', 'b'), false);
    return input;
  }

  public void nextWord() {
    GuiUtil.executeOnSwingWorker(() -> model.runNextWord());
  }

  public void nextWords(int amount) {
    GuiUtil.executeOnSwingWorker(
        () -> {
          model.runWords(amount);
          renderManager.thinOutRenderingQueue(FRAMES_TO_KEEP);
        });
  }

  public void remainingWords() {
    GuiUtil.executeOnSwingWorker(() -> model.runRemainingWords());
  }
}
