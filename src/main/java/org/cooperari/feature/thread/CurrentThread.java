package org.cooperari.feature.thread;

import org.cooperari.core.CThread;

/**
 * Operation for sending a (simulated) spurious wakeup to another thread.
 * 
 * @since 0.2
 *
 */
public final class CurrentThread {    
  /**
   * Execute {@link Thread#currentThread()} operation.
   * @param thisThread This thread.
   * @return The state of the target thread.
   */
  public static Thread execute(CThread thisThread) {
    thisThread.cYield(CThread.NOP);
    Thread vt = thisThread.getVirtualizedThread();
    return (vt != null) ? vt : thisThread;
  }
}
