package ganz.leonard.automatalearning.automata.tools;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import java.io.BufferedReader;
import java.io.IOException;

public class Minimizer<T> {

  // I know that implementing a common minimization algorithm would most likely be a lot easier but
  // let's instead have some fun with python :)

  public static <T> DeterministicFiniteAutomaton<T> minimize(DeterministicFiniteAutomaton<T> dfa)
      throws IOException, InterruptedException {

    GreeneryNotationConverter<T> notationConverter = new GreeneryNotationConverter<>();
    // fix path (src/main/python should not be needed)
    String scriptPath = "src/main/python/ganz/leonard/automatalearning/automata_tools/minimize.py";
    BufferedReader in =
        PythonScriptUtil.pythonScriptProcessSetup(
            scriptPath, notationConverter.toGreeneryNotation(dfa, true));
    DeterministicFiniteAutomaton<T> minimizedDfa = null;
    while (in.ready()) {
      String line = in.readLine();
      System.out.println(line);
      if (line.startsWith("fsm")) {
        minimizedDfa = notationConverter.fromGreeneryNotation(line);
      }
    }

    return minimizedDfa;
  }
}
