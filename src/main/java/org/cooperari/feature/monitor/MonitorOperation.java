package org.cooperari.feature.monitor;

import org.cooperari.core.COperation;
import org.cooperari.core.CThread;

/**
 * Base class for monitor operations.
 * 
 * @since 0.2
 *
 * @param <T> Type for operation result.
 */
abstract class MonitorOperation<T> extends COperation<T> {
  /**
   * Thread.
   */
  protected final CThread _thread;

  /**
   * Monitor.
   */
  protected final Monitor _monitor;

  /**
   * Constructor.
   * @param t thread
   * @param m Monitor.
   */ 
  protected MonitorOperation(CThread t, Monitor m) {
    super(m);
    _thread = t;
    _monitor = m;
  }

}
