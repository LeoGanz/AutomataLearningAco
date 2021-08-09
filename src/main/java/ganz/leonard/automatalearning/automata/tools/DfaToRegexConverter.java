package ganz.leonard.automatalearning.automata.tools;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import java.io.BufferedReader;
import java.io.IOException;

public class DfaToRegexConverter {

  public static <T> String convert(DeterministicFiniteAutomaton<T> dfa)
      throws IOException, InterruptedException {
    GreeneryNotationConverter<T> notationConverter = new GreeneryNotationConverter<>();
    // fix path (src/main/python should not be needed)
    String scriptPath = "src/main/python/ganz/leonard/automatalearning/automata_tools/regex.py";
    BufferedReader in =
        PythonScriptUtil.pythonScriptProcessSetup(
            scriptPath, notationConverter.toGreeneryNotation(dfa, false));
    String regex = null;
    while (in.ready()) {
      String line = in.readLine();
      System.out.println(line);
      String prefix = "regex: ";
      if (line.startsWith(prefix)) {
        regex = line.substring(prefix.length());
      }
    }

    return regex;
  }
}
