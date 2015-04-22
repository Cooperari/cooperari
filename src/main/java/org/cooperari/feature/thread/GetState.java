package org.cooperari.feature.thread;

import org.cooperari.core.CThread;

/**
 * Operation for sending a (simulated) spurious wakeup to another thread.
 * 
 * @since 0.2
 *
 */
public  final class GetState {    
 

  /**
   * Execute {@link Thread#getState()} operation.
   * @param thisThread This thread.
   * @param targetThread Target thread.
   * @return The state of the target thread.
   */
  public static Thread.State execute(CThread thisThread, Thread targetThread) {
    thisThread.cYield(CThread.NOP);
    return  Feature.getCThread(targetThread).getVirtualState();
  }
}
