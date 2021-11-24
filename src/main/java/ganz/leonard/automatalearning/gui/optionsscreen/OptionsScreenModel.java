package ganz.leonard.automatalearning.gui.optionsscreen;

import ganz.leonard.automatalearning.language.Language;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OptionsScreenModel {
  private final PropertyChangeSupport pcs;
  private boolean generateSamples;
  private boolean automaticColonySize;
  private Path selectedInputFile;
  private Language<?> selectedGeneratingLanguage;

  public OptionsScreenModel() {
    pcs = new PropertyChangeSupport(this);
    resetToDefaults();
  }

  public void resetToDefaults() {
    generateSamples = true;
    automaticColonySize = true;
    selectedInputFile = InputProvider.getAvailableInputFiles().stream().findFirst().orElse(null);
    selectedGeneratingLanguage =
        InputProvider.getAvailableGeneratingLanguages().stream().findFirst().orElse(null);
    notifyListeners();
  }

  public Path getSelectedInputFile() {
    return selectedInputFile;
  }

  public void setSelectedInputFile(Path selectedInputFile) {
    this.selectedInputFile = selectedInputFile;
    notifyListeners();
  }

  public Language<?> getSelectedGeneratingLanguage() {
    return selectedGeneratingLanguage;
  }

  public void setSelectedGeneratingLanguage(Language<Character> selectedGeneratingLanguage) {
    this.selectedGeneratingLanguage = selectedGeneratingLanguage;
    notifyListeners();
  }

  public boolean isGenerateSamples() {
    return generateSamples;
  }

  public void setGenerateSamples(boolean generateSamples) {
    this.generateSamples = generateSamples;
    notifyListeners();
  }

  public boolean isAutomaticColonySize() {
    return automaticColonySize;
  }

  public void setAutomaticColonySize(boolean automaticColonySize) {
    this.automaticColonySize = automaticColonySize;
    notifyListeners();
  }

  public Set<Path> getAvailableInputFiles() {
    return InputProvider.getAvailableInputFiles();
  }

  public Set<Language<?>> getAvailableGeneratingLanguages() {
    return InputProvider.getAvailableGeneratingLanguages();
  }

  public Map<List<Object>, Boolean> getChosenInput(int sampleNoForGeneration)
      throws IOException, URISyntaxException {
    if (generateSamples) {
      if (selectedGeneratingLanguage == null) {
        throw new IllegalStateException(
            "No language was selected or none is available for generation");
      }
      return InputProvider.generateSamples(selectedGeneratingLanguage, sampleNoForGeneration);
    } else {
      if (selectedInputFile == null) {
        throw new IllegalStateException("No input file was selected or none is available");
      }
      return InputProvider.readFromFile(selectedInputFile);
    }
  }

  public void addPropertyChangeListener(PropertyChangeListener changeListener) {
    pcs.addPropertyChangeListener(changeListener);
  }

  private void notifyListeners() {
    pcs.firePropertyChange("OptionsScreen", null, this);
  }
}
