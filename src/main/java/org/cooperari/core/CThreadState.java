package org.cooperari.core;

/**
 * Cooperative thread state.
 * 
 * <p>
 * The state abstracts away the actual thread state at the JVM level and
 * provides the thread's state from the logical perspective of cooperative
 * scheduling.
 * </p>
 * 
 * @since 0.2
 */
public enum CThreadState {
  /**
   * Initializing.
   */
  CINITIALIZING(Thread.State.NEW),
  
  /**
   * Ready (meaning it is blocked at a yield point at the JVM level).
   */
  CREADY(Thread.State.RUNNABLE),

  /**
   * Running.
   */
  CRUNNING(Thread.State.RUNNABLE),

  /**
   * Blocked.
   */
  CBLOCKED(Thread.State.BLOCKED),

  /**
   * Waiting.
   */
  CWAITING(Thread.State.WAITING),
  
  /**
   * Timed waiting.
   */
  CTIMED_WAITING(Thread.State.TIMED_WAITING),

  /**
   * Terminated.
   */
  CTERMINATED(Thread.State.TERMINATED);

  /**
   * The corresponding virtualized state.
   */
  private final Thread.State _vState;

  /**
   * Private constructor.
   * 
   * @param vState Virtualized state.
   */
  private CThreadState(Thread.State vState) {
    _vState = vState;
  }

  /**
   * Get {@link java.lang.Thread.State} corresponding to the state.
   * @return Virtualized thread state.
   */
  public Thread.State getVirtualState() {
    return _vState;
  }
}