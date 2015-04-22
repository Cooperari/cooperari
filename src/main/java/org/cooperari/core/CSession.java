package org.cooperari.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashSet;

import org.cooperari.CTest;
import org.cooperari.CTestResult;
import org.cooperari.CYieldPoint;
import org.cooperari.config.CCoverage;
import org.cooperari.config.CMaxTrials;
import org.cooperari.config.CTimeLimit;
import org.cooperari.config.CTraceOptions;
import org.cooperari.core.aspectj.AgentFacade;
import org.cooperari.core.util.CReport;
import org.cooperari.errors.CCheckedExceptionError;
import org.cooperari.errors.CConfigurationError;
import org.cooperari.errors.CHotspotError;
import org.cooperari.errors.CInternalError;
import org.cooperari.feature.hotspots.HotspotHandler;

/**
 * Test session executor.
 * 
 * @since 0.2
 *
 */
public final class CSession {

  /**
   * Exception handler.
   */
  public interface ExecutionHandler {
    /**
     * Callback method invoked in case test trial completed normally (without
     * exceptions).
     * 
     * @throws Throwable In case an error needs to be reported by the handler.
     */
    void onNormalCompletion() throws Throwable;

    /**
     * Callback method invoked in case test trial completes with an exception.
     * 
     * @param e Exception.
     * @return <code>true</code> if exception should be ignored.
     */
    boolean ignoreException(Throwable e);
  }

  /**
   * Execute a test session.
   * 
   * *
   * <p>
   * The test class parameter identifies the application code at stake in some
   * client-dependent manner. The class will be used as a source of
   * configuration annotations (those defined in the <code>org.cooperari</code>
   * package).
   * </p>
   * 
   * <p>
   * The supplied {@link Runnable} instance defines the entry point for the test
   * code. The test session will begin by creating a thread using that entry
   * point.
   * 
   * When the entry point thread and any other application thread transitively
   * created by it are done, the test session terminates.
   * 
   * Test failures are signaled by exceptions thrown either by application
   * threads or the internal logic.
   * </p>
   * 
   * 
   * @param test Test.
   * @return An instance of {@link CTestResult}.
   */
  public static CTestResult executeTest(CTest test) {
    assert CWorkspace.debug("== STARTED %s ==", test.getName());
    CRuntime runtime = new CRuntime(new CConfiguration(test.getConfiguration()));
    CCoverage coverage = runtime.getConfiguration(CCoverage.class);
    CTraceOptions traceOptions = runtime.getConfiguration(CTraceOptions.class);

    CoveragePolicy cHandler;
    try {
      Constructor<? extends CoveragePolicy> c = coverage.value()
          .getImplementation().getConstructor(CRuntime.class);
      cHandler = c.newInstance(runtime);
    } catch (Exception e) {
      throw new CInternalError(e);
    }

    CMaxTrials maxTrials = runtime.getConfiguration(CMaxTrials.class);

    if (maxTrials.value() < 0) {
      throw new CConfigurationError("Invalid @CMaxTrials configuration: "
          + maxTrials.value());
    }

    int trials = 0;
    Throwable failure;
    long timeLimit = runtime.getConfiguration(CTimeLimit.class).value() * 1000L;

    HotspotHandler hHandler = new HotspotHandler(runtime);
    runtime.register(hHandler);

    // Main loop
    long startTime = System.currentTimeMillis();
    boolean done = false;
    CoverageLog clog = new CoverageLog();
    CTrace trace = new CTrace(clog, traceOptions);
    runtime.register(trace);

    do {
      failure = null;
      trials++;
      trace.clear();
      hHandler.startTestTrial();
      cHandler.onTestStarted();
      CScheduler s = new CScheduler(runtime, cHandler, test);
      s.start();
      try {
        s.join();
      } catch (InterruptedException e) {
        throw new CInternalError(e);
      }
      
      cHandler.onTestFinished();
      try {
        s.rethrowExceptionsIfAny();
        hHandler.endTestTrial();
        try {
          test.onNormalCompletion();
        } catch (Throwable e) {
          failure = e;
        }
      } catch (Throwable e) {
        assert CWorkspace.debug(Thread.currentThread(), e);
        if (test.ignoreException(e) == false) {
          failure = e;
        }
      }
      if (failure == null && traceOptions.logEveryTrace()) {
        saveTrace(test, trials, trace);
      }
      done = failure != null
          || cHandler.done()
          || trials >= maxTrials.value()
          || (timeLimit > 0 && System.currentTimeMillis() - startTime >= timeLimit);
    } while (!done);

    File traceFile = null;
    
    if (failure != null) {
      traceFile = saveTrace(test, trials, trace);
    } else {
      try {
        hHandler.endTestSession();
      } catch (CHotspotError e) {
        failure = e;
      }
    }
    trace.clear();
    long timeElapsed = System.currentTimeMillis() - startTime;

    assert CWorkspace.debug("== TERMINATED %s ==", test.getName());

    CWorkspace.log("%s: executed %d trials in %d ms [%s]", test.getName(),
        trials, timeElapsed, failure == null ? "passed" : "failed : "
            + failure.getClass().getCanonicalName());

    if (failure instanceof CCheckedExceptionError) {
      failure = failure.getCause();
    }

    AgentFacade.INSTANCE.enrichCoverageInfo(clog);

    return new CTestResultImpl(failure != null, trials, timeElapsed, failure,
        traceFile, clog.getYieldPointsCovered(), clog.getYieldPointCount() - clog.getYieldPointsCovered());
  }

  @SuppressWarnings("javadoc")
  private static File saveTrace(CTest test, int trialNumber, CTrace trace) {
    try {
      CReport report = CWorkspace.INSTANCE.createReport(test.getSuiteName() + '_' + test.getName() + "_trial_" + trialNumber);
      try { 
        trace.save(report);
        CWorkspace.log("Trace for trial %d of %s written to '%s'", trialNumber, test.getName(),
            report.getFile().getAbsolutePath());
        return report.getFile();
      } finally {
        report.close();
      }
    } catch (Throwable e) {
      CWorkspace.log("Error trace file for %s: %s", 
                     test.getName(), e.getMessage());
      CWorkspace.log(e);
      return null;
    } 
  }

  @SuppressWarnings("javadoc")
  private static class CTestResultImpl implements CTestResult {
    final int _trials;
    final boolean _failed;
    final Throwable _failure;
    final File _failureTrace;
    final long _executionTime;
    final int _coveredYieldPoints;
    final int _uncoveredYieldPoints;

    CTestResultImpl(boolean failed, int trials, long timeElapsed,
        Throwable failure, File traceFile, int coveredYieldPoints, int uncoveredYieldPoints) {
      _failed = failed;
      _trials = trials;
      _executionTime = timeElapsed;
      _failure = failure;
      _failureTrace = traceFile;
      _coveredYieldPoints = coveredYieldPoints;
      _uncoveredYieldPoints = uncoveredYieldPoints;
    }

    @Override
    public boolean failed() {
      return _failed;
    }

    @Override
    public Throwable getFailure() {
      return _failure;
    }

    @Override
    public File getFailureTrace() {
      return _failureTrace;
    }

    @Override
    public int trials() {
      return _trials;
    }

    @Override
    public long getExecutionTime() {
      return _executionTime;
    }
    
    @Override 
    public int getCoveredYieldPoints() {
      return _coveredYieldPoints;
    }

    @Override 
    public int getTotalYieldPoints() {
      return _coveredYieldPoints + _uncoveredYieldPoints;
    }
    
  }

}
