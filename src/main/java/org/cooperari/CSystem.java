package org.cooperari;

import static org.cooperari.core.CRuntime.getRuntime;

import java.io.File;
import java.io.IOException;

import org.cooperari.config.CAlways;
import org.cooperari.config.CNever;
import org.cooperari.config.CSometimes;
import org.cooperari.core.CSession;
import org.cooperari.core.aspectj.AgentFacade;
import org.cooperari.errors.CHotspotError;
import org.cooperari.errors.CNonCooperativeModeError;
import org.cooperari.feature.hotspots.HotspotHandler;
import org.cooperari.preemptive.NonCooperativeThreadRunner;

/**
 * Cooperari system facade.
 * 
 * <p>
 * Note that some methods can only be called from code that is already 
 * running cooperatively, for instance from a JUnit test method that is being executed
 * using Cooperari's custom JUnit runner ({@link org.cooperari.junit#CJUnitRunner}).
 * These methods will throw {@link org.cooperari.errors.CNonCooperativeModeError} otherwise.</p>.
 * </p>
 * @since 0.2
 *
 */
public final class CSystem {

  /**
   * Execute a cooperative test session. 
   * @param test Test to execute.
   * @return An instance of {@link CTestResult}. 
   */
  public CTestResult executeTest(CTest test) {
    return CSession.executeTest(test);
  }

  /**
   * Start some threads and wait until they are finished ("fork and join").
   * 
   * The threads will execute cooperatively if cooperative semantics
   * are enabled, otherwise execution will run under normal (preemptive
   * semantics). 
   *  
   * @param runnables Runnable instances.
   * @throws IllegalThreadStateException if any of the runnable instances is
   *          an already alive {@link Thread} object.
   * @throws CNonCooperativeModeError If the method is invoked by a non-cooperative thread.
   */
  public static void forkAndJoin(Runnable... runnables) {
    new NonCooperativeThreadRunner(runnables);
  }

  /**
   * Signal that a hotspot has been reached.
   * 
   * <p>
   * Place a call to this method in application code to mark a point where an
   * hotspot of interest has been reached. Reachability conditions may be marked
   * in test methods using reachability annotations like {@link CAlways},
   * {@link CSometimes}, and {@link CNever}.
   * </p>
   * 
   * @param id Hotspot id.
   * @throws CHotspotError if <code>id</code> is a {@link CNever} hotspot.
   * @see #hotspot(String, boolean)
   */
  public static void hotspot(String id) throws CHotspotError {
    getRuntime().get(HotspotHandler.class).onHotspotReached(id);
  }

  /**
   * Signal that a hotspot has been reached, subject to a boolean condition.
   * <p>
   * <code>cHotspot(id, condition);</code> is equivalent to
   * <code>if (condition) cHotspot(id);</code>
   *
   * @param id Hotspot id.
   * @param condition Boolean condition value.
   * @throws CHotspotError if the <code>condition == true</code> and
   *         <code>id</code> is a {@link CNever} hotspot.
   * @see #hotspot(String)
   */
  public static void hotspot(String id, boolean condition)
      throws CHotspotError {
    if (condition) {
      hotspot(id);
    }
  }

  /**
   * Simulate a spurious thread wakeup.
   * 
   * @param t Target thread.
   * @throws CNonCooperativeModeError If the method is invoked by a non-cooperative thread.
   */
  public static void sendSpuriousWakeup(Thread t) {
    throw new CNonCooperativeModeError();
  }

  /**
   * Get global coverage information for all tests executed so far.
   * @return An instance of {@link CCoverage}.
   */
  public static CCoverage getGlobalCoverageInfo() {
    return AgentFacade.INSTANCE.getGlobalCoverageLog();
  }

  /**
   * Generate a global coverage report.
   * @return {@link File} object indicating the generated report.
   * @throws IOException If an I/O error occurs.
   */
  public static File generateGlobalCoverageReport() throws IOException {
    return AgentFacade.INSTANCE.produceCoverageReport();
  }

  /**
   * Check if the system is in cooperative mode.
   * @return <code>true</code> if cooperative semantics are enabled.
   */
  public static boolean inCooperativeMode() {
    return AgentFacade.INSTANCE.isActive();
  }
  
  @SuppressWarnings("javadoc")
  private CSystem() {

  }



}
