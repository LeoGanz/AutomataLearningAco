package ganz.leonard.automatalearning.gui.optionsscreen;

import java.nio.file.Path;

public record PathWrapper(Path path) {
  @Override
  public String toString() {
    return path.getFileName().toString();
  }
}
