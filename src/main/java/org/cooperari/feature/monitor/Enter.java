package org.cooperari.feature.monitor;

import static org.cooperari.core.CRuntime.getRuntime;
import static org.cooperari.core.CThreadState.CBLOCKED;
import static org.cooperari.core.CThreadState.CREADY;

import org.cooperari.core.CThread;
import org.cooperari.core.CThreadState;

/**
 * Monitor enter operation (lock acquisition).
 * 
 * @since 0.2
 *
 */
final class Enter extends MonitorOperation<Void> {    

  /**
   * Constructor.
   * @param t thread
   * @param m Monitor.
   */ 
  Enter(CThread t, Monitor m) {
    super(t, m);
    _monitor.addReference();
    if (m.getOwner() != t) {
      DeadlockDetector dd = getRuntime().get(DeadlockDetector.class);
      if (dd != null) {
        dd.onMonitorEnter(t, m);
      }
    }
  }

  /**
   * Get operation state.
   * Blocked state will be indicated while monitor is unlocked,
   * unless thread already owns the lock.
   * @return <code>CREADY</code> when ready,  <code>CBLOCKED</code> otherwise.
   */
  @Override
  public CThreadState getState() {
    CThread tOwner = _monitor.getOwner();
    return tOwner == null || tOwner == _thread ? 
        CREADY : CBLOCKED;
  }

  /**
   * Complete operation by acquiring the lock.
   */
  @Override
  public void execute() {
    _monitor.enter(_thread);
  }
  
  /**
   * Execute operation.
   * @param t This thread.
   * @param o Object being locked.
   */
  public static void execute(CThread t, Object o) {
    if (o != null) {
      Monitor m = getRuntime().get(MonitorPool.class).get(o, true);
      t.cYield(new Enter(t,m));
    }
  }

}

