package org.cooperari.core;

import java.io.File;
import java.io.IOException;

import org.cooperari.CSystem;
import org.cooperari.CTest;
import org.cooperari.CTestResult;
import org.cooperari.config.CGenerateCoverageReports;
import org.cooperari.config.CMaxTrials;
import org.cooperari.config.CScheduling;
import org.cooperari.config.CTimeLimit;
import org.cooperari.config.CTraceOptions;
import org.cooperari.core.aspectj.AgentFacade;
import org.cooperari.core.scheduling.CScheduler;
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
   * Current test (workaround).
   */
  private static CTest _currentTest = null;
  
  /**
   * Current runtime (workaround).
   */
  private static CRuntime _currentRuntime = null;

  /**
   * Test mutex (workaround).
   */
  private static final Object TEST_EXEC_MUTEX = new Object();

  /**
   * Execute a test session.
   * 
   * *
   * <p>
   * The test class parameter identifies the application code at stake in some
   * client-dependent manner. The class will be used as a source of
   * configuration annotations (those defined in the <code>org.cooperari.config</code>
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
   * @param test The test.
   * @return An instance of {@link CTestResult}.
   */
  public static CTestResult executeTest(CTest test) {
    synchronized(TEST_EXEC_MUTEX) {
      assert CWorkspace.debug("== PREPARING  %s ==", test.getName());
      CTestResult r;
      try {
        _currentTest = test;
        _currentRuntime = new CRuntime(new CConfiguration(test.getConfiguration()));
        if (CSystem.inCooperativeMode()) {
          r = executeTestCooperatively(test);
        } else {
          r = executeTestPreemptively(test);
        }
        return r;
      } finally {
        _currentTest = null;
        _currentRuntime = null;
      }
    }
  }

  /**
   * Get current test being executed.
   * @return The current test being executed.
   */
  public static CTest getCurrentTest() {
    return _currentTest;
  }
  
  /**
   * Get runtime environment for current test.
   * @return The current runtime environment.
   */
  public static CRuntime getRuntime() {
    return _currentRuntime;
  }
  
  /**
   * Execute a test with cooperative semantics.
   * @param test The test.
   * @return The test result.
   */
  private static CTestResult executeTestCooperatively(CTest test) {
    assert CWorkspace.debug("== STARTED %s (cooperatively) ==", test.getName());
  
    CScheduling schConfig = _currentRuntime.getConfiguration(CScheduling.class);
    CTraceOptions traceOptions = _currentRuntime.getConfiguration(CTraceOptions.class);

    CScheduler scheduler = schConfig.schedulerFactory().create();

    CMaxTrials maxTrials = _currentRuntime.getConfiguration(CMaxTrials.class);

    if (maxTrials.value() < 1) {
      throw new CConfigurationError("Invalid @CMaxTrials configuration: "
          + maxTrials.value());
    }

    int trials = 0;
    Throwable failure;
    long timeLimit = _currentRuntime.getConfiguration(CTimeLimit.class).value() * 1000L;

    HotspotHandler hHandler = new HotspotHandler(_currentRuntime);
    _currentRuntime.register(hHandler);

    // Main loop
    long startTime = System.currentTimeMillis();
    boolean done = false;
    CCoverageLog clog = new CCoverageLog();
    CTrace trace = new CTrace(clog, traceOptions);
    _currentRuntime.register(trace);

    do {
      failure = null;
      trials++;
      trace.reset();
      hHandler.startTestTrial();
      scheduler.onTestStarted();
      CEngine s = new CEngine(_currentRuntime, scheduler, test);
      s.start();
      try {
        s.join();
      } catch (InterruptedException e) {
        throw new CInternalError(e);
      }

      scheduler.onTestFinished();
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
          || !scheduler.continueTrials()
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

    AgentFacade.INSTANCE.complementCoverageInfo(clog);

    if (_currentRuntime.getConfiguration(CGenerateCoverageReports.class).value()) {
      try {
        clog.produceCoverageReport(test.getSuiteName(), test.getName());
      } 
      catch(IOException e) {
        throw new CInternalError(e);  
      }
    }
    return new CTestResultImpl(trials, timeElapsed, clog, failure, traceFile);
  }

  /**
   * Execute a test with preemptive semantics.
   * @param test The test.
   * @return The test result.
   */
  private static CTestResult executeTestPreemptively(CTest test) {
    assert CWorkspace.debug("== STARTED %s (preemptively) ==", test.getName());
    _currentTest = test;
    CMaxTrials maxTrials = _currentRuntime.getConfiguration(CMaxTrials.class);

    if (maxTrials.value() < 1) {
      throw new CConfigurationError("Invalid @CMaxTrials configuration: "
          + maxTrials.value());
    }

    int trials = 0;
    Throwable failure;
    long timeLimit = _currentRuntime.getConfiguration(CTimeLimit.class).value() * 1000L;

    HotspotHandler hHandler = new HotspotHandler(_currentRuntime);
    _currentRuntime.register(hHandler);

    // Main loop
    long startTime = System.currentTimeMillis();
    boolean done = false;

    do {
      assert CWorkspace.debug("== TRIAL %d of %s ==", trials, test.getName());
      failure = null;
      trials++;
      hHandler.startTestTrial();

      try {
        test.run();
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

      done = failure != null
          || trials >= maxTrials.value()
          || (timeLimit > 0 && System.currentTimeMillis() - startTime >= timeLimit);
    } while (!done);


    long timeElapsed = System.currentTimeMillis() - startTime;

    assert CWorkspace.debug("== TERMINATED %s ==", test.getName());

    CWorkspace.log("%s: executed %d trials in %d ms [%s]", test.getName(),
        trials, timeElapsed, failure == null ? "passed" : "failed : "
            + failure.getClass().getCanonicalName());

    if (failure instanceof CCheckedExceptionError) {
      failure = failure.getCause();
    }

    return new CTestResultImpl(trials, timeElapsed, null, failure, null);
  }

  @SuppressWarnings("javadoc")
  private static File saveTrace(CTest test, int trialNumber, CTrace trace) {
    try {
      CReport report = CWorkspace.INSTANCE.createReport(test.getSuiteName(), test.getName() + "_trial_" + trialNumber);
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
    final long _executionTime;
    final int _yieldPoints;
    final int _yieldPointsCovered;
    final Throwable _failure;
    final File _failureTrace;

    CTestResultImpl(int trials, long timeElapsed, CCoverageLog clog,
        Throwable failure, File failureTrace) {
      _trials = trials;
      _executionTime = timeElapsed;
      _yieldPoints = clog != null ? clog.getTotalYieldPoints() : 0;
      _yieldPointsCovered = clog != null ? clog.getCoveredYieldPoints() : 0;
      _failure = failure;
      _failureTrace = failureTrace;
    }

    @Override
    public boolean failed() {
      return _failure != null;
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
    public Throwable getFailure() {
      return _failure;
    }

    @Override
    public File getFailureTrace() {
      return _failureTrace;
    }


    @Override 
    public int getCoveredYieldPoints() {
      return _yieldPointsCovered;
    }

    @Override 
    public int getTotalYieldPoints() {
      return _yieldPoints;
    }
  }

}
