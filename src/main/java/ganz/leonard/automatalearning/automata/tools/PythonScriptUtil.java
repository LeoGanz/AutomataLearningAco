package ganz.leonard.automatalearning.automata.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.SystemUtils;

public class PythonScriptUtil {

  // fix only working on windows
  private static final String PYTHON_INTERPRETER = ".gradle/python/Scripts/python.exe";

  public static BufferedReader processSetup(String baseCmd, String scriptPath, List<String> args)
      throws IOException, InterruptedException {

    if (!SystemUtils.IS_OS_WINDOWS) {
      throw new UnsupportedOperationException(
          "Executing scripts on operating systems other than windows "
              + "is unfortunately not yet supported");
    }

    List<String> cmd = new ArrayList<>();
    cmd.add(baseCmd);
    cmd.add(scriptPath);
    cmd.addAll(args);
    ProcessBuilder pb = new ProcessBuilder(cmd);
    pb.redirectErrorStream(true);
    Process p = pb.start();
    p.waitFor();

    return new BufferedReader(new InputStreamReader(p.getInputStream()));
  }

  public static BufferedReader pythonScriptProcessSetup(String scriptPath, List<String> args)
      throws IOException, InterruptedException {
    return processSetup(PYTHON_INTERPRETER, scriptPath, args);
  }
}
