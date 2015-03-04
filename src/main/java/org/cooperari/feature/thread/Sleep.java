package org.cooperari.feature.thread;

import static org.cooperari.core.CThreadState.CREADY;
import static org.cooperari.core.CThreadState.CTIMED_WAITING;

import org.cooperari.core.CThread;
import org.cooperari.core.CThreadState;

/**
 * Cooperative operation for {@link Thread#sleep(long)} and
 * {@link Thread#sleep(long,int)}.
 * 
 * @since 0.2
 *
 */
public final class Sleep extends ThreadOperation<Boolean> {
  /**
   * This thread.
   */
  private final CThread _thisThread;

  /**
   * Sleep deadline in nanoseconds (<code>System.nanoTime()</code> wall time).
   */
  private final long _deadline;

  /**
   * Interrupt flag.
   */
  private boolean _interrupted;

  /**
   * Constructor.
   * 
   * @param thisThread Current thread.
   * @param timeout Sleep timeout.
   */
  Sleep(CThread thisThread, long timeout) {
    super(timeout);
    _thisThread = thisThread;
    _deadline = timeout < 0L ? timeout : System.nanoTime() + timeout;
    _interrupted = false;
  }

  /**
   * Get state.
   * 
   * @return {@link CThreadState#CREADY} if sleep deadline expires or thread is
   *         interrupted, {@link CThreadState#CTIMED_WAITING} otherwise.
   */
  @Override
  public CThreadState getState() {
    if (_deadline < 0L || _interrupted) {
      return CREADY;
    }
    if (_thisThread.testAndClearInterruptStatus()) {
      _interrupted = true;
      return CREADY;
    }
    return System.nanoTime() - _deadline >= 0 ? CREADY : CTIMED_WAITING;
  }

  /**
   * Complete operation.
   */
  @Override
  public void execute() {
    if (_deadline < 0L) {
      throw new IllegalArgumentException("Invalid sleep timeout.");
    }
  }

  /**
   * Get completion result.
   * 
   * @return <code>false</code> if sleep completed normally, <code>true</code>
   *         if interrupted.
   */
  @Override
  public Boolean getResult() {
    return _interrupted;
  }

  /**
   * Execute sleep operation.
   * 
   * @param thisThread This thread.
   * @param timeout Sleep timeout in nanoseconds.
   * @throws InterruptedException In case thread was interrupted while sleeping.
   */
  public static void execute(CThread thisThread, long timeout)
      throws InterruptedException {
    if (thisThread.cYield(new Sleep(thisThread, timeout))) {
      throw new InterruptedException();
    }
  }
}
