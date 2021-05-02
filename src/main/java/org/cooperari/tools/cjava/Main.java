//
//Copyright 2014-2019 Eduardo R. B. Marques
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//
package org.cooperari.tools.cjava;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.cooperari.CSystem;
import org.cooperari.CTestResult;
import org.cooperari.app.CApplication;
import org.cooperari.core.CSession;
import org.cooperari.core.CWorkspace;
import org.cooperari.errors.CInternalError;

/**
 * Main class for the <code>capp</code> utility program.
 * 
 * @since 0.4
 */
public final class Main {

  /**
   * Exit code when all tests passed.
   */
  private static final int ALL_TESTS_PASSED = 0;

  /**
   * Exit code when some tests failed.
   */
  private static final int SOME_TESTS_FAILED = 1;

  /**
   * Exit code for invalid arguments.
   */
  private static final int INVALID_ARGUMENTS = 2;

  /**
   * Exit code for unexpected internal error.
   */
  private static final int INTERNAL_ERROR = 3;

  /**
   * Program entry point. 
   * 
   * @param args Arguments.
   */
  public static void main(String[] args) {
    int exitCode = execute(args, System.out);
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
      if (args == null || args.length < 2) {
        out.println("Invalid arguments!");
        return INVALID_ARGUMENTS;
      }

      if (! CWorkspace.INSTANCE.isInitialized()) {
        CWorkspace.INSTANCE.initialize(new File(args[0]));
      }

      String javaClassName = args[1];
      Class<?>  javaClass; 

      try {
        javaClass = Class.forName(javaClassName);
      }
      catch (ClassNotFoundException e) {
        out.println(javaClassName + ": class not found!");
        return INVALID_ARGUMENTS;
      }

      CApplication cap = new CApplication(javaClass, Arrays.copyOfRange(args, 2, args.length));

      if (! CSystem.inCooperativeMode()) {
        out.println("Execution will be preemptive, AspectJ LTW is not active.");
      }

      CTestResult result = CSession.executeTest(cap);
      displayTestResult(result, out);
      return result.failed() ? SOME_TESTS_FAILED : ALL_TESTS_PASSED;
    } 
    catch (Throwable e) {
      out.println("Unexpected error!");
      e.printStackTrace(out);
      return INTERNAL_ERROR;
    }
  }
  
  @SuppressWarnings("javadoc")
  private static void displayTestResult(CTestResult result, PrintStream out) {
    if (! CSystem.inCooperativeMode()) {
      out.printf("    > trials: %d time: %d ms", 
          result.trials(), result.getExecutionTime());
    } else {
      out.printf("    > trials: %d time: %d ms coverage: %4.1f %% (%d / %d yp)", 
          result.trials(), result.getExecutionTime(),
          result.getCoverageRate(), result.getCoveredYieldPoints(), result.getTotalYieldPoints());

      if (result.failed() && result.getFailureTrace() != null) {
        out.println();
        try {
          out.printf("    > failure trace: '%s'", result.getFailureTrace().getCanonicalPath());
        } catch (IOException e) {
          throw new CInternalError(e);
        }
      }
    }
    out.println();
  }

  /**
   * Private constructor to avoid instantiation.
   */
  private Main() {

  }

}