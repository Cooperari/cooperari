package org.cooperari.feature.thread;

import org.cooperari.core.COperation;

/**
 * Base class for operations involving thread methods.
 *
 * @param <T> Result type
 * @since 0.2
 */
public abstract class ThreadOperation<T> extends COperation<T> {

  /**
   * Constructs a thread operation with no arguments.
   * @see org.cooperari.core.COperation#COperation()
   */
  public ThreadOperation() {

  }

  /**
   * Constructs a thread operation with specified arguments.
   * @param arguments Operation arguments.
   * @see org.cooperari.core.COperation#COperation(Object[])
   */
  public ThreadOperation(Object... arguments) {
    super(arguments);
  }

}
