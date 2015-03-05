package org.cooperari.core;


import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.cooperari.CCoverage;
import org.cooperari.CHotspotError;
import org.cooperari.CInternalError;
import org.cooperari.CMaxTrials;
import org.cooperari.CTimeLimit;
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
     * Callback method invoked in case test trial completed normally (without exceptions).
     * @throws Throwable In case an error needs to be reported by the handler.
     */
    void onNormalCompletion() throws Throwable;

    /**
     * Callback method invoked in case test trial completes with an exception.
     * @param e Exception.
     * @return <code>true</code> if exception should be ignored.
     */
    boolean ignoreException(Throwable e);
  }

  /**
   * Execute a test session.
   * 
   * * <p>
   * The test class parameter identifies the application code at stake in some client-dependent manner.
   * The class will be used as a source of configuration annotations (those defined in the <code>org.cooperari</code> package).
   * </p>
   * 
   * <p>
   * The supplied {@link Runnable} instance defines the entry point 
   * for the test code. The test session will begin by creating a
   * thread using that entry point. 
   * 
   * When the entry point thread and any other application thread transitively created by it 
   * are done, the test session terminates.
   * 
   * Test failures are signaled by exceptions thrown either by application
   * threads or the internal logic.
   * </p>
   * 
   * 
   * @param config Configuration source.
   * @param entryPoint Entry point for test code.
   * @param executionHandler Execution handler.
   * @throws Throwable Exception during execution.
   */
  public static void execute(AnnotatedElement config, Runnable entryPoint, ExecutionHandler executionHandler) throws Throwable {
    assert CWorkspace.debug("== STARTED %s ==", config.toString());
    CRuntime runtime = new CRuntime(new CConfiguration(config));
    CCoverage coverage = runtime.getConfiguration(CCoverage.class);
    CoveragePolicy cHandler;
    try {
      Constructor<? extends CoveragePolicy> c = coverage.value().getImplementation().getConstructor(CRuntime.class);
      cHandler = c.newInstance(runtime);
    } catch (Exception e) {
      throw new CInternalError(e);
    }
    
    CMaxTrials maxTrials = runtime.getConfiguration(CMaxTrials.class);
    int runs = 0;
    Throwable error = null;
    long timeLimit = runtime.getConfiguration(CTimeLimit.class).value() * 1000L;
    CTrace trace = null;
    HotspotHandler hHandler = new HotspotHandler(runtime);
    runtime.register(hHandler);
    
    // Main loop
    long startTime = System.currentTimeMillis();
    boolean done = false;
    
    do {
      runs++;
      hHandler.startTestTrial();
      cHandler.onTestStarted();
      CScheduler s = new CScheduler(runtime, cHandler, entryPoint);
      trace = s.getTrace();
      runtime.register(trace);
      s.start();
      s.join();
      cHandler.onTestFinished();
      try { 
        s.rethrowExceptionsIfAny();
        hHandler.endTestTrial();
        try {
          executionHandler.onNormalCompletion();
        } catch (Throwable e) {
          error = e;
        }
      } catch (Throwable e) {
        assert CWorkspace.debug(Thread.currentThread(), e);
        if (executionHandler.ignoreException(e) == false) {
          error = e;
        }
      }
      done = error != null 
          || cHandler.done() 
          || runs >= maxTrials.value()
          || ( timeLimit > 0 
          && System.currentTimeMillis() - startTime >= timeLimit);   
    } while (!done);

    if (error != null) {
      dumpTrace((Method) config, trace);
    } else {
      try {
        hHandler.endTestSession();
      } catch (CHotspotError e) {
        error = e;
      }
    }

    long timeElapsed = System.currentTimeMillis() - startTime;

    assert CWorkspace.debug("== TERMINATED %s ==", config.toString());

    CWorkspace.log("%s: executed %d times in %d ms [%s]", 
        config.toString(), runs, timeElapsed, error == null ? "passed" : "failed : " + error.getClass().getCanonicalName());
    
    if (error != null) {
      throw error;
    }
  }

  @SuppressWarnings("javadoc")
  private static void dumpTrace(Method m, CTrace trace) {
    try {
      Class<?> testClass = m.getDeclaringClass();
      File logDir = new File("log");
      logDir.mkdirs();
      File file = 
        new File(logDir, testClass.getName() + "_" + m.getName() + ".trace.txt");
      trace.write(file);
      CWorkspace.log("Failure trace for %s written to '%s'", m.getName(), file.getAbsolutePath());
    } catch(Exception e) {
      CWorkspace.log("Error writing failure trace for %s to '%s'", m.getName(), e.getMessage());
      CWorkspace.log(e);
    }
  }

}
