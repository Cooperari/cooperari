package org.cooperari;

import static org.cooperari.core.CRuntime.getRuntime;

import org.cooperari.feature.hotspots.HotspotHandler;

/**
 * Utility methods to interface with the Cooperari system.
 * 
 * @since 0.2
 *
 */
public final class CSystem {

  /**
   * Start some threads and wait until they are finished.
   * 
   * @param runnables Runnable instances.
   * @throws IllegalThreadStateException if any of the runnable instances is
   *         actually an already alive {@link Thread} object.
   */
  public static void cRun(Runnable... runnables) {
    throw new CInternalError("Call not handled as a yield point!");
  }

  /**
   * Signal that a hotspot has been reached.
   * <p>
   * Place a call to this method in application code to mark a point where an
   * hotspot of interest has been reached. Reachability conditions may be marked
   * in test methods using reachability annotations like {@link CAlways},
   * {@link CSometimes}, or {@link CNever}.
   * </p>
   * 
   * @param id Hotspot id.
   * @throws CHotspotError if <code>id</code> is a {@link CNever} hotspot.
   * @see #cHotspot(String, boolean)
   */
  public static void cHotspot(String id) throws CHotspotError {
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
   * @see #cHotspot(String)
   */
  public static void cHotspot(String id, boolean condition)
      throws CHotspotError {
    if (condition) {
      cHotspot(id);
    }
  }

  /**
   * Trigger a spurious wakeup for given thread.
   * 
   * @param t Target thread.
   */
  public static void cSpuriousWakeup(Thread t) {
    throw new CInternalError("Call not handled as a yield point!");
  }

  @SuppressWarnings("javadoc")
  private CSystem() {

  }

}
