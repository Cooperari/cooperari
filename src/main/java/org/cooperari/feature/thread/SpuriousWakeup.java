package org.cooperari.feature.thread;

import org.cooperari.core.CThread;

/**
 * Operation for sending a (simulated) spurious wakeup to another thread.
 * 
 * @since 0.2
 *
 */
public  final class SpuriousWakeup extends ThreadOperation<Void> {    
 
  /**
   * Target thread.
   */
  private final CThread _targetThread;
  
  /**
   * Constructor.
   * @param targetThread Target thread.
   */ 
  private SpuriousWakeup(CThread targetThread) {
    _targetThread = targetThread;
  }
  
  /**
   * Execute operation
   */
  @Override
  public void execute() {
    _targetThread.triggerSpuriousWakeup();
  }


  /**
   * Execute spurious wakeup operation.
   * @param thisThread This thread.
   * @param targetThread Target thread for spurious wakeup.
   */
  public static void execute(CThread thisThread, Thread targetThread) {
    thisThread.cYield(new SpuriousWakeup(Feature.getCThread(targetThread)));
  }
}
