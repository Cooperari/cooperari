package org.cooperari.tools.cjunit;

import java.io.File;
import java.io.PrintStream;

import org.cooperari.CCoverage;
import org.cooperari.CSystem;
import org.cooperari.CTestResult;
import org.cooperari.CVersion;
import org.cooperari.junit.CTestResultPool;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * JUnit run listener for <code>cjunit</code>.
 * 
 * @since 0.2 
 */
class CJUnitRunListener extends RunListener {

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
    _out = out;
  }

  /**
   * JUnit test-run hook.
   * @param description Test description.
   */
  @Override
  public void testRunStarted(Description description) throws Exception {
    _out.printf("== Cooperari %s - JUnit test execution  ==", CVersion.ID);
    _out.println();
    _currentClassName = "";
  }


  /**
   * JUnit test-run finished hook.
   * @param result Test result.
   */
  @Override
  public void testRunFinished(Result result) {
    if (result.getFailureCount() > 0) {
      _out.println("== Failure details ==");
      int i = 0;
      for (Failure f : result.getFailures()) {
        _out.print(++i);
        _out.print(") ");
        _out.println(f.getTestHeader());
        _out.print(f.getTrace());
      }
    }
    _out.println("== Summary ==");
    _out.printf("Executed: %d; Skipped: %d;  Failed: %d; Execution time: %d ms%n", 
        result.getRunCount(), 
        result.getIgnoreCount(), 
        result.getFailureCount(),
        result.getRunTime());
    _out.println("== Global coverage ==");
    CCoverage ci = CSystem.getGlobalCoverageInfo();

    _out.printf("Coverage rate: %4.1f %% (%d / %d yp)%n", 
        ci.getCoverageRate(),
        ci.getCoveredYieldPoints(),
        ci.getTotalYieldPoints());
    try {
      File report = CSystem.generateGlobalCoverageReport();
      _out.printf("Global coverage report: '%s'%n", report.getAbsolutePath());
    }
    catch (Throwable e) {
      e.printStackTrace(_out);
    }
  }


  /**
   * JUnit atomic test start hook.
   * @param description Test description.
   */
  @Override
  public void testStarted(Description description) {
    if (!description.getClassName().equals(_currentClassName)) {
      _currentClassName = description.getClassName();
      _out.println(_currentClassName);
    }
    _out.printf("  %-55s ", description.getMethodName());
  }

  /**
   * JUnit atomic test finish hook.
   * @param description Test description.
   */
  @Override
  public void testFinished(Description description) {
    _out.println("[passed]");
    displayTestDetails(description);
  }

  /**
   * JUnit atomic test finish hook.
   * @param failure Failure.
   */
  @Override
  public void testFailure(Failure failure) {
    _out.printf("[failed: %s]", failure.getException().getClass().getCanonicalName());
    _out.println();
    displayTestDetails(failure.getDescription());
  }


  @SuppressWarnings("javadoc")
  private void displayTestDetails(Description description) {
    CTestResult result = CTestResultPool.INSTANCE.getTestResult(description);
    if (result == null) {
      return;
    }
    _out.printf("    > trials: %d time: %d ms coverage: %4.1f %% (%d / %d yp)", 
        result.trials(), result.getExecutionTime(),
        result.getCoverageRate(), result.getCoveredYieldPoints(), result.getTotalYieldPoints());

    if (result.failed() && result.getFailureTrace() != null) {
      _out.println();
      _out.printf("    > failure trace: '%s'", result.getFailureTrace().getAbsolutePath());
    }
    _out.println();
  }

  /**
   * JUnit atomic test skip hook.
   * @param description Test description.
   */
  @Override
  public void testIgnored(Description description) {
    _out.printf("  %-55s [skipped]", description.getMethodName());
    _out.println();
  }

}
