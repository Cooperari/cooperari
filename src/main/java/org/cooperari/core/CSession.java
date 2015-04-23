package org.cooperari.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

import org.cooperari.CTest;
import org.cooperari.CTestResult;
import org.cooperari.config.CGenerateCoverageReports;
import org.cooperari.config.CMaxTrials;
import org.cooperari.config.CScheduling;
import org.cooperari.config.CTimeLimit;
import org.cooperari.config.CTraceOptions;
import org.cooperari.core.aspectj.AgentFacade;
import org.cooperari.core.util.CReport;
import org.cooperari.errors.CCheckedExceptionError;
import org.cooperari.errors.CConfigurationError;
import org.cooperari.errors.CError;
import org.cooperari.errors.CHotspotError;
import org.cooperari.errors.CInternalError;
import org.cooperari.feature.hotspots.HotspotHandler;
import org.cooperari.scheduling.CScheduler;

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
    CScheduling schConfig = runtime.getConfiguration(CScheduling.class);
    CTraceOptions traceOptions = runtime.getConfiguration(CTraceOptions.class);

    CScheduler scheduler;
    try {
      Constructor<? extends CScheduler> c = 
          schConfig.scheduler().getConstructor(CRuntime.class);
      scheduler = c.newInstance(runtime);
    }
    catch (CError e) {
      throw e;
    }
    catch (Throwable e) {
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
      scheduler.onTestStarted();
      CEngine s = new CEngine(runtime, scheduler, test);
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
          || scheduler.continueTrials()
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
    
    if (runtime.getConfiguration(CGenerateCoverageReports.class).value()) {
      try {
        clog.produceCoverageReport(test.getSuiteName(), test.getName());
      } 
      catch(IOException e) {
        throw new CInternalError(e);  
      }
    }
    return new CTestResultImpl(trials, timeElapsed, clog, failure, traceFile);
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

    CTestResultImpl(int trials, long timeElapsed, CoverageLog clog,
        Throwable failure, File failureTrace) {
      _trials = trials;
      _executionTime = timeElapsed;
      _yieldPoints = clog.getTotalYieldPoints();
      _yieldPointsCovered = clog.getCoveredYieldPoints();
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
