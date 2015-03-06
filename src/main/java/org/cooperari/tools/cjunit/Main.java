package org.cooperari.tools.cjunit;

import java.io.File;
import java.io.PrintStream;

import org.cooperari.core.CWorkspace;
import org.cooperari.core.aspectj.AgentFacade;
import org.cooperari.junit.CJUnitRunListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Main class for the <code>cjunit</code> utility program.
 * 
 * @since 0.2
 */
public class Main {

  /**
   * Private constructor to avoid instantiation.
   */
  private Main() {
  }

  /**
   * Program entry point. 
   * 
   * @param args Arguments.
   */
  public static void main(String[] args) {
    int exitCode;
    try {
      exitCode = execute(args, System.out);
    } catch (Throwable e) {
      e.printStackTrace();
      System.out.println("Unexpected error.");
      exitCode = 2;
    }
    System.exit(exitCode);
  }

  /**
   * Execution delegate. 
   * 
   * <p>This behaves like <code>main</code> but can be invoked programmatically.</p>
   * 
   * @param args Program arguments.
   * @param out Output stream.
   * @return A suggested program exit code, <code>0</code> for normal termination,
   *         non-zero in the case of errors.
   */
  public static int execute(String[] args, PrintStream out) {
    try {      
      if (! AgentFacade.INSTANCE.isActive()) {
        out.println("The AspectJ load-time weaver does not seem to be active!");
        return 2;
      }
      
      if (!CWorkspace.INSTANCE.isInitialized()) {
        CWorkspace.INSTANCE.initialize(new File("."));
      }
      
      Class<?>[] classes = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) {
        classes[i] = Class.forName(args[i]);
      }
      
      JUnitCore juc = new JUnitCore();

      juc.addListener(new CJUnitRunListener(System.out));
      Result r = juc.run(classes);
      
      CWorkspace.log("Yield points covered: %d out of %d\n", AgentFacade.INSTANCE.getWeavePointsCovered(), AgentFacade.INSTANCE.getWeavePointCount());
      
      AgentFacade.INSTANCE.produceCoverageReport();
      
      return r.getFailureCount() == 0 ? 0 : 1;
    } catch (Throwable e) {
      e.printStackTrace(out);
      return 3;
    }
  }
}
