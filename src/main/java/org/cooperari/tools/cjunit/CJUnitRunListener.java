package org.cooperari.tools.cjunit;

import java.io.PrintStream;

import org.cooperari.CVersion;
import org.cooperari.core.CWorkspace;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.internal.TextListener;

/**
 * Custom JUnit run listener for <code>cjunit</code>.
 * 
 * @since 0.2 
 */
class CJUnitRunListener extends TextListener {

  /**
   * Output stream.
   */
  private final PrintStream _out;
  
  /**
   * Last class. 
   */
  private String _currentClassName;
  
  /**
   * Constructs a new listener.
   * @param out Output stream.
   */
  public CJUnitRunListener(PrintStream out) {
    super(out);
    _out = out;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void testRunStarted(Description description) throws Exception {
    _out.printf("== Cooperari %s - JUnit test execution  ==", CVersion.ID);
    _out.println();
    _currentClassName = "";
  }
  
  @Override
  public void testRunFinished(Result result) {
      if (result.getFailureCount() > 0) {
        _out.println("== Failure details ==");
        printFailures(result);
      }
      _out.println("== Execution summary ==");
      _out.printf("  Executed: %d\n  Ignored: %d\n  Failed: %d\n  Execution time: %d ms\n", 
          result.getRunCount(), 
          result.getIgnoreCount(), 
          result.getFailureCount(),
          result.getRunTime());
    

  }

  @Override
  public void testStarted(Description description) {
    if (!description.getClassName().equals(_currentClassName)) {
      _currentClassName = description.getClassName();
      _out.println(_currentClassName);
    }
    _out.printf("  %-50s ", description.getMethodName());
  }
  
  @Override
  public void testFinished(Description description) {
     _out.println("[passed]");
  }

  @Override
  public void testFailure(Failure failure) {
    _out.printf("[failed : %s ]", failure.getException().getClass().getCanonicalName());
    _out.println();
  }

  @Override
  public void testIgnored(Description description) {
    _out.printf("  %-50s [ignored]", description.getMethodName());
    _out.println();
  }

}
