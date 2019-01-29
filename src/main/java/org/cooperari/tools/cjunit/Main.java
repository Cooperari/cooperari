//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.cooperari.tools.cjunit;

import java.io.File;
import java.io.PrintStream;

import org.cooperari.CSystem;
import org.cooperari.core.CWorkspace;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Main class for the <code>cjunit</code> utility program.
 * 
 * @since 0.2
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
      
      Class<?>[] classes = new Class<?>[args.length-1];
      for (int i = 1; i < args.length; i++) {
        try {
          classes[i-1] = Class.forName(args[i]);
        }
        catch (ClassNotFoundException e) {
          out.println(args[i] + ": class not found!");
          return INVALID_ARGUMENTS;
        }
      }
      
      if (! CSystem.inCooperativeMode()) {
        out.println("Execution will be preemptive, AspectJ LTW is not active.");
      }
      
      JUnitCore juc = new JUnitCore();
      juc.addListener(new CJUnitRunListener(System.out));
      Result r = juc.run(classes);   
      return r.getFailureCount() == 0 ? ALL_TESTS_PASSED : SOME_TESTS_FAILED;
    } 
    catch (Throwable e) {
      out.println("Unexpected error!");
      e.printStackTrace(out);
      return INTERNAL_ERROR;
    }
  }
  
  /**
   * Private constructor to avoid instantiation.
   */
  private Main() {
    
  }

}
