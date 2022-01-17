package ganz.leonard.automatalearning.gui.alscreen.inputwords;

import ganz.leonard.automatalearning.gui.RenderManager;
import java.awt.Dimension;
import javax.swing.JScrollPane;

public class ScrollableWordList<T> extends JScrollPane {

  public static final int PADDING_FOR_SCROLLBAR = 25;
  private final WordList<T> wordList;

  public ScrollableWordList(WordList<T> wordList) {
    super(
        wordList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    this.wordList = wordList;
    setViewportBorder(null);
    setBorder(null);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(
        (int) wordList.getPreferredSize().getWidth() + PADDING_FOR_SCROLLBAR,
        RenderManager.IMAGE_HEIGHT);
  }
}
