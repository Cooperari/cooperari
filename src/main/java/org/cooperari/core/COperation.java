package org.cooperari.core;

import org.cooperari.core.scheduling.CThreadLocation;

/**
 * Base class for cooperative operations.
 * 

 * 
 * 
 * <p>Subclasses should override when necessary the following methods:</p>
 * <ul>
 * <li>{@link #getState()}, if the operation is subject to blocking;</li>
 * <li>{@link #execute()}, if the operation has associated actions -- operations
 * that merely introduce yield points before proceeding to application code should
 * not need to;</li>
 * <li>{@link #getResult()}, if the operation has an associated result, when <code>T</code> is not {@link Void};</li>
 * <li>and, finally, {@link #getAbortOperation()} if the operation requires cleanup actions on
 * thread death.</li>
 * </ul>
 *
 * 
 * <p>
 * The <code>_arguments</code> field is intentionally <code>protected</code>,
 * and forms the base for the implementations of {@link #equals(Object)} as
 * {@link #hashCode()} --both of which play an important role in program state
 * representation/abstraction -- as well as {@link #toString()} in this class.
 * These three methods cannot be overridden by subclasses (they are
 * <code>final</code>).
 * </p>
 *
 * @param <T> Type for operation result. It is usually parameterized as
 *        {@link Void}.
 *        
 * @since 0.2
 */
public abstract class COperation<T> {

  /**
   * Empty array constant for operations with no arguments.
   */
  private static final Object[] NO_ARGS = new Object[0];

  /**
   * Cached hash code.
   */
  private int _hash;

  /**
   * Operation arguments.
   */
  protected final Object[] _arguments;

  /**
   * Constructor w/no arguments.
   */
  protected COperation() {
    this(NO_ARGS);
  }

  /**
   * Constructor.
   * 
   * @param arguments Operation arguments.
   */
  protected COperation(Object... arguments) {
    _hash = 0;
    _arguments = arguments;
  }

  /**
   * Get operation state. This method can be called by ANY thread. The default
   * implementation signals the thread as ready.
   * 
   * @return {@link CThreadState#CREADY}
   */
  public CThreadState getState() {
    return CThreadState.CREADY;
  }

  /**
   * Execute the operation. The method is guaranteed to run within the
   * cooperative thread the operation associates to. The default operation does
   * nothing.
   */
  public void execute() {

  }

  /**
   * Get operation result. The method is guaranteed to run within the
   * cooperative thread the operation associates to.
   * 
   * @return The default result is <code>null</code>
   */
  public T getResult() {
    return null;
  }

  /**
   * Get abort operation. The abort operation aids the thread death procedure,
   * by performing any necessary prior actions to the actual "death sequence".
   * The method should be overridden when abrupt termination requires a prior
   * operation to do some cleanup.
   * 
   * @return A {@link COperation} object. The base implementation returns a
   *         no-op abort action that allows thread death to proceed immediately.
   */
  public COperation<?> getAbortOperation() {
    return DUMMY_ABORT;
  }

  /**
   * Default abort action. It immediately signal ready state ande executes
   * nothing.
   */
  private static final COperation<?> DUMMY_ABORT = new COperation<Void>() {
    
  };

  /**
   * Get yield point stage for this operation.
   * The base implementation returns 0.
   * @return The stage for the yield point this operation relates to.
   * @see CThreadLocation
   */
  public int getStage() {
    return 0;
  }
  
  /**
   * Get hash code.
   * 
   * @return A hash code value based on the object's operation class and
   *         operation arguments.
   * @see java.lang.Object#hashCode()
   */
  public final int hashCode() {
    int h = _hash;
    if (h == 0) {
      h = getClass().hashCode();
      for (Object a : _arguments) {
        h ^= (a == null ? 0 : a.hashCode());
      }
    }
    return _hash;
  }

  /**
   * Test for equality against given object reference. Two operations are
   * considered equivalent if they are of the same class and have the same
   * arguments.
   * 
   * @return <code>true</code> if and only if <code>o</code> is a reference to
   *         an equivalent operation.
   * @see Object#equals(Object)
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final COperation<?> other = (COperation<?>) o;

    if (_hash != other._hash) {
      return false;
    }

    final Object[] args1 = _arguments;
    final int n = args1.length;
    final Object[] args2 = other._arguments;

    if (n != args2.length) {
      return false;
    }

    for (int i = 0; i != n; i++) {
      Object o1 = args1[i];
      Object o2 = args2[i];
      // Optimized to avoid calls to equals() as much as possible.
      if (o1 != o2 && (o1 == null || o2 == null || !o1.equals(o2))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Get textual representation of the operation. The method is used for
   * debugging purposes.
   * 
   * @return A {@link String} object.
   */
  @Override
  public final String toString() {
    StringBuilder sb = new StringBuilder(getClass().getSimpleName());
    sb.append('(');
    boolean first = true;
    for (Object a : _arguments) {
      if (!first)
        sb.append(',');
      if (a == null) {
        sb.append("null");
      } else if (a instanceof CThread) {
        sb.append(((CThread) a).getCID());
      } else {
        sb.append(a.toString());
      }
      first = false;
    }
    sb.append(')');
    return sb.toString();
  }

 

}
