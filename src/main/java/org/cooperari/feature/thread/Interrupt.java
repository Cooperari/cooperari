package org.cooperari.feature.thread;

import org.cooperari.core.CThread;

/**
 * Cooperative operation for {@link Thread#interrupt()}.
 * 
 * @see Thread#interrupt()
 */
public final class Interrupt extends ThreadOperation<Void> {
  /**
   * Thread to interrupt.
   */
  private final CThread _targetThread;
  
  /**
   * Constructor.
   * @param targetThread Thread to interrupt.
   */ 
  private Interrupt(CThread targetThread) {
    super(targetThread);
    _targetThread = targetThread;
  }
 
  /**
   * Complete by interrupting the thread.
   */
  @Override
  public void execute() {
    _targetThread.triggerInterrupt();
  }
  
  /**
   * Execute thread interrupt operation.
   * @param thisThread This thread.
   * @param targetThread Thread to interrupt.
   */
  public static void execute(CThread thisThread, Thread targetThread) {
    thisThread.cYield(new Interrupt(CThreadFeature.getCThread(targetThread)));
  }
}