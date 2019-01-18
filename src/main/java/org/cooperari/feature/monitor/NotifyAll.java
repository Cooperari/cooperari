package org.cooperari.feature.monitor;

import static org.cooperari.core.CRuntime.getRuntime;

import org.cooperari.core.CThread;

/**
* Operation for {@link Object#notifyAll()}.
* 
* @since 0.2
*/
public final class NotifyAll extends MonitorOperation<Void> {
  /**
   * Constructor.
   * @param t Thread.
   * @param m Monitor.
   */
  NotifyAll(CThread t, Monitor m) {
    super(t, m);
  }


  @Override
  public void execute() {
    if (_monitor == Monitor.NULL) {
      throw new NullPointerException();
    }
    if (_monitor.getOwner() != _thread) {
      throw new IllegalMonitorStateException("Monitor is not owned by current thread.");
    }
    _monitor.notifyAllThreads();
  }


  /**
   * Execute operation.
   * @param t Current thread.
   * @param o Target object.
   * @throws IllegalMonitorStateException As described for <code>Object.notifyAll</code>.
   */
  public static void execute(CThread t, Object o)  throws IllegalMonitorStateException {
    MonitorPool ms = getRuntime().get(MonitorPool.class);
    Monitor m = ms.get(o);
    t.cYield(new NotifyAll(t, m));
  }
}