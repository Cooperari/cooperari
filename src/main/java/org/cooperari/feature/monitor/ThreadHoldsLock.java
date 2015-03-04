package org.cooperari.feature.monitor;

import static org.cooperari.core.CRuntime.getRuntime;

import org.cooperari.core.CThread;

/**
 * Operation for <code>Thread.holdsLock()</code>.
 * 
 * @author Eduardo Marques, DI/FCUL, 2014/15
 */
public final class ThreadHoldsLock extends MonitorOperation<Boolean> {
  /**
   * Constructor.
   * @param t thread
   * @param m Monitor.
   */ 
  ThreadHoldsLock(CThread t, Monitor m) {
    super(t, m);
  }
  /* (non-Javadoc)
   * @see cooperari.core.COperation#getResult()
   */
  @SuppressWarnings("javadoc")
  @Override
  public Boolean getResult() {
    if (_monitor == Monitor.NULL) {
      throw new NullPointerException();
    }
    return _monitor.getOwner() == _thread;
  }
  
  /**
   * Execute <code>Thread.holdsLock()</code>.
   * @param t This thread.
   * @param o Object.
   * @return Boolean value as described for <code>Thread.holdsLock(Object)</code>.
   * @throws NullPointerException As described for <code>Thread.holdsLock(Object)</code>.
   */
  public static boolean execute(CThread t, Object o) throws NullPointerException {
    MonitorPool ms = getRuntime().get(MonitorPool.class);
    return t.cYield(new ThreadHoldsLock(t, ms.get(o)));
  }
}
