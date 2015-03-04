package org.cooperari.feature.thread;

import org.cooperari.core.CThread;

/**
 * Cooperative operation for {@link Thread#interrupted()}.
 * 
 * @since 0.2
 */
public final class Interrupted extends ThreadOperation<Boolean> {
  /**
   * Thread to query interruption.
   */
  private final CThread _thread;

  /**
   * Result after completion.
   */
  private boolean _result;

  /**
   * Constructor.
   * @param thread Thread to interrupt.
   */ 
  Interrupted(CThread thread) {
    super();
    _thread = thread;
    _result = false;
  }

  /**
   * Complete by clearing the interrupt status of the thread
   * and getting previous status.
   */
  @Override
  public void execute() {
    _result = _thread.testAndClearInterruptStatus();
  }

  /**
   * Get result, i.e., the previous interrupted status for the thread.
   * @return A <code>Boolean</code> object.
   */
  public Boolean getResult() {
    return _result;
  }

  /**
   * Execute an operation of this kind.
   * @param t Thread to interrupt (should be the current thread always).
   * @return Interruption status.
   */
  public static boolean execute(CThread t) {
    // Note: t will be the current thread.
    return t.cYield(new Interrupted(t));
  }
}
