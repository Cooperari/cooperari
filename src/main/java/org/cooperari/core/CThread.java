package org.cooperari.core;

import static org.cooperari.core.CThreadState.CBLOCKED;
import static org.cooperari.core.CThreadState.CINITIALIZING;
import static org.cooperari.core.CThreadState.CREADY;
import static org.cooperari.core.CThreadState.CRUNNING;
import static org.cooperari.core.CThreadState.CTERMINATED;
import static org.cooperari.core.CThreadState.CWAITING;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.LockSupport;

import org.aspectj.lang.JoinPoint;
import org.cooperari.core.scheduling.CThreadHandle;
import org.cooperari.core.scheduling.CThreadLocation;
import org.cooperari.core.scheduling.CYieldPoint;
import org.cooperari.errors.CInternalError;

/**
 * Cooperative thread.
 * 
 * @since 0.2
 */
public final class CThread extends Thread implements CThreadHandle {

  // CLASS CONSTANTS
  /**
   * Operation constant for a no-op.
   */
  public static final COperation<Void> NOP = new COperation<Void>() {
  };

  /**
   * Operation constant to indicate thread has just been created.
   */
  private static final COperation<Void> INIT = new COperation<Void>() {
    @Override
    public CThreadState getState() {
      return CINITIALIZING;
    }
  };

  /**
   * Operation constant to denote thread has just been started.
   */
  private static final COperation<Void> START = new COperation<Void>() {

  };

  /**
   * Operation constant to denote thread has finished
   */
  private static COperation<Void> TERMINATED = new COperation<Void>() {
    @Override
    public CThreadState getState() {
      return CTERMINATED;
    }
  };

  /**
   * Operation constant to denote thread is not at an yield point.
   */
  private static final COperation<Void> NOT_AT_YIELD_POINT = new COperation<Void>() {
  };

  // INSTANCE FIELDS
  /**
   * Cooperative thread id.
   */
  private final int _cid;

  /**
   * Runnable object.
   */
  private final Runnable _runnable;

  /**
   * Engine reference.
   */
  private final CEngine _engine;

  /**
   * Association to explicitly started thread instance (if any).
   */
  private final Thread _virtualizedThread;

  /**
   * Condition variable for cooperative yields.
   */
  private boolean _atYieldPoint = false;

  /**
   * Current join point, if any.
   */
  private CYieldPoint _yieldPoint;

  /**
   * Thread's yield point.
   */
  private CThreadLocation _location;

  /**
   * Pending operation for yield point.
   */
  private COperation<?> _operation;

  /**
   * Interrupt event logical time (0 if interrupt is not set, positive otherwise).
   */
  private int _interruptTime;

  /**
   * Spurious wakeup event time (-1 if spurious wakeups are disabled, 0 if enabled but not received, greater than 0 if received).
   */
  private int _spuriousWakeupTime;

  /**
   * Thread step (incremented upon each yield point).
   */
  private int _step = 0;
  
  /**
   * Death sequence flag.
   */
  private boolean _dying;

  /**
   * Resumption barrier.
   */
  private final CyclicBarrier _resumptionBarrier = new CyclicBarrier(2);

  /**
   * Logical clock for events.
   */
  private int _eventClock = 0;

  // CONSTRUCTOR
  /**
   * Constructs a new cooperative thread.
   * 
   * @param e Cooperative execution engine.
   * @param r Runnable to be executed by the thread.
   * @param cid Cooperative execution id for the thread.
   */
  public CThread(CEngine e, final Runnable r, int cid) {
    setDaemon(true);
    _cid = cid;
    _operation = INIT;
    _location = new CThreadLocation(CYieldPointImpl.THREAD_INITIALIZATION);
    _engine = e;
    _runnable = r;
    _virtualizedThread = r instanceof Thread ? (Thread) r : null;
    if (_virtualizedThread == null) {
      setName("<" + _cid + ">");
    } else {
      e.getRuntime().get(CThreadMappings.class).associate(_virtualizedThread, this);
      setName(_virtualizedThread.getName());
    }
  }

  // METHODS
  /**
   * Execution method.
   */
  @Override
  public void run() {
    _engine.getRuntime().join();
    try {
      _location = new CThreadLocation(CYieldPointImpl.THREAD_STARTED_YIELD_POINT);
      cYield(START);
      _runnable.run();
    } catch (ThreadDeath death) {
      // handled silently in line with the specs
    } catch (Error | RuntimeException ex) {
      assert CWorkspace.debug(CThread.this, ex);
      throw ex;
    } finally {
      _operation = TERMINATED;
      _location = new CThreadLocation(CYieldPointImpl.THREAD_TERMINATED_YIELD_POINT);
      _engine.getRuntime().leave();
    }
  }

  /**
   * Get cooperative thread ID.
   * 
   * @return The thread's ID.
   */
  @Override
  public int getCID() {
    return _cid;
  }

  /**
   * Get current step count.
   * @return The number of yield points handled by the thread so far.
   */
  public int getStep() {
    return _step;
  }
  
  /**
   * Get the thread's current location.
   * 
   * @return The current thread location.
   */
  @Override
  public CThreadLocation getLocation() {
    return _location;
  }

  /**
   * Get engine that governs the execution of this thread.
   * 
   * @return A {@link CEngine} instance.
   */
  public CEngine getEngine() {
    return _engine;
  }

  /**
   * Get virtualised thread for this cooperative thread, if any.
   * 
   * @return A {@link Thread} instance or <code>null</code>.
   */
  public Thread getVirtualizedThread() {
    return _virtualizedThread;
  }
  
  /**
   * Get runnable instance for this class.
   * 
   * @return A {@link Runnable} instance.
   */
  public Runnable getRunnable() {
    return _runnable;
  }

  /**
   * Test if thread is stopped in a yield point.
   * 
   * @return Yield point status.
   */
  public boolean atYieldPoint() {
    return _atYieldPoint;
  }

  /**
   * Intercept thread, if a CThread.
   * 
   * @param jp Join point.
   * @return A <code>CThread</code> if current thread is cooperative,
   *         <code>null</code> otherwise.
   */
  public static CThread intercept(JoinPoint jp) {
    CThread ct = self();
    if (ct != null) {
      ct._yieldPoint = new CYieldPointImpl(jp.getStaticPart());
    }
    return ct;
  }

  /**
   * Get current thread object if the thread is cooperative, <code>null</code> otherwise.
   * <p>
   * Callers can use this method
   * to determine if they are executing in a cooperative context.
   * Note also that the returned thread may be masking 
   * an actual application thread.  The latter can be queried
   * by {@link #getVirtualizedThread()}.
   * </p>
   * 
   * @return The thread object or <code>null</code> if the thread is non-cooperative.
   */
  public static CThread self() {
    Thread t = Thread.currentThread();
    return t instanceof CThread ? (CThread) t : null;
  }
  
  /**
   * Get logical running state of thread.
   * 
   * @return The logical running state of the thread.
   */
  public CThreadState getCState() {
    if (_atYieldPoint == true) {
      return _operation.getState();
    }
    switch (super.getState()) {
      case NEW:
        return CINITIALIZING;
      case RUNNABLE:
        return CRUNNING;
      case TERMINATED:
        return CTERMINATED;
      case BLOCKED:
      case WAITING:
      case TIMED_WAITING:
        // This should not happen, ideally! It will mean relevant yield points
        // are not covered.
        return CRUNNING;
    }
    throw new CInternalError();
  }

  /**
   * Get virtual thread state.
   * 
   * @return The virtual thread state.
   */
  public Thread.State getVirtualState() {
    return getCState().getVirtualState();
  }

  /**
   * Check is thread is (logically) running (CRUNNING state).
   * 
   * @return true if thread is running.
   */
  public boolean isRunning() {
    return getCState() == CRUNNING;
  }

  /**
   * Check is thread is (logically) ready to be executed (CREADY state).
   * 
   * @return true if thread is ready.
   */
  public boolean isReady() {
    return getCState() == CREADY;
  }

  /**
   * Check is thread is blocked (CBLOCKED state).
   * 
   * @return true if thread is blocked.
   */
  public boolean isBlocked() {
    return getCState() == CBLOCKED;
  }

  /**
   * Check is thread is waiting (CWAITING state).
   * 
   * @return true if thread is waiting.
   */
  public boolean isWaiting() {
    return getCState() == CWAITING;
  }

  /**
   * Check is thread is terminated (CTERMINATED state).
   * 
   * @return true if thread is terminated.
   */
  public boolean isTerminated() {
    return getCState() == CTERMINATED;
  }

  /**
   * Cooperatively yield (new version).
   * 
   * @param <T> Type of result for operation.
   * @param op Operation.
   * @return Result for operation.
   */
  public <T> T cYield(COperation<T> op) {

    // Initiate yield sequence.
    if (_yieldPoint != null) {
      _location = new CThreadLocation(_yieldPoint, op.getStage());
    } 

    _operation = op;
    assert CWorkspace.debug("yielding - %s", toString());

    // Yield.
    _engine.wakeup();
    _atYieldPoint = true;
    do {
      assert CWorkspace.debug("parking");
      LockSupport.park();
      assert CWorkspace.debug("unparked");
    } while (_atYieldPoint);

    // Execute operation.
    assert CWorkspace.debug("resumed - %s", toString());
    RuntimeException rtExc = null;
    Error errorExc = null;

    try {
      _operation.execute();
      assert CWorkspace.debug("executed op - %s", toString());
    } catch (RuntimeException e) {
      assert CWorkspace.debug("op throwed up %s - %s ", e.getClass(), toString());
      rtExc = e;
    } catch (Error e) {
      assert CWorkspace.debug("op throwed up %s - %s ", e.getClass(), toString());
      errorExc = e;
    }
    // Sync step.
    assert CWorkspace.debug("syncing - %s", toString());
    _step++;
    _operation = NOT_AT_YIELD_POINT;

    try {
      _resumptionBarrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      throw new CInternalError(e);
    }

    assert CWorkspace.debug("fully resumed [%s]", getLocation());

    // Return sequence.
    if (rtExc != null) {
      throw rtExc;
    }
    if (errorExc != null) {
      throw errorExc;
    }
    return op.getResult();
  }

  /**
   * Resume the thread after a cooperative yield. This is executed by the
   * cooperative execution engine only. 
   * The thread will be allowed to resume and
   * complete the operation for the current yield point.
   */
  public void cResume() {
    if (_atYieldPoint == false)
      throw new CInternalError();
    _atYieldPoint = false;
    LockSupport.unpark(this);
    try {
      _resumptionBarrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      throw new CInternalError(e);
    }
  }

  /**
   * Special operation to initiate thread stop sequence.
   */
  private static final class Die extends COperation<Void> {
    /**
     * Previous operation, that will be aborted.
     */
    final COperation<?> _abortOp;

    /**
     * Terminal exception.
     */
    private final Throwable _exception;

    /**
     * Constructor.
     * 
     * @param op Abort operation
     * @param e Terminating exception.
     */
    Die(COperation<?> op, Throwable e) {
      super(op, e);
      _abortOp = op;
      _exception = e;
    }

    /**
     * Report state as indicated by abort operation.
     */
    @Override
    public CThreadState getState() {
      return _abortOp.getState();
    }

    /**
     * Execute by aborting current operation and throwing the terminal
     * exception.
     */
    @Override
    public void execute() {
      assert CWorkspace.debug("executing abort op");
      _abortOp.execute();
      if (_exception instanceof RuntimeException)
        throw (RuntimeException) _exception;
      if (_exception instanceof Error)
        throw (Error) _exception;
      throw new CInternalError(_exception);
    }
  }

  /**
   * Order thread to stop.
   * 
   * @param e Throwable instance.
   */
  public void cStop(Throwable e) {
    if (getCState() != CTERMINATED && !_dying) {
      _dying = true;
      _location = new CThreadLocation(_location.getYieldPoint(), -1);
      _operation = new Die(_operation.getAbortOperation(), e);
    }
  }

  // EVENT HANDLING
  /**
   * Create new event and increment logical clock for events.
   * 
   * @return A {#link CEvent} object.
   */
  private int newEventTime() {
    return ++_eventClock;
  }

  /**
   * Test and clear interrupt status.
   * 
   * @return <code>true</code> if thread was interrupted.
   */
  public boolean testAndClearInterruptStatus() {
    boolean prevStatus = getInterruptStatus();
    _interruptTime = 0;
    return prevStatus;
  }
  
  /**
   * Test and clear wakeup events. Use this method when spurious wakeups are enabled.
   * @return <code>null</code>, {@link CBlockingOperationEvent#INTERRUPTION_EVENT}, or {@link CBlockingOperationEvent#SPURIOUS_WAKEUP_EVENT}.
   */
  public CBlockingOperationEvent testAndClearWakeupEvents() {
    CBlockingOperationEvent r = null;
    if (_spuriousWakeupTime > 0 && _spuriousWakeupTime > _interruptTime) {
      r = CBlockingOperationEvent.SPURIOUS_WAKEUP_EVENT;
      _spuriousWakeupTime = -1; // do not clear interrupt status
    } else if (_interruptTime > 0) {
      r = CBlockingOperationEvent.INTERRUPTION_EVENT;
      _spuriousWakeupTime = -1;
      _interruptTime = 0;
    } 
    return r;
  }

  /**
   * Trigger interrupt. The request is ignored if the interrupt status is
   * already set.
   */
  public void triggerInterrupt() {
    if (_interruptTime == 0) {
      _interruptTime = newEventTime();
    }
  }

  /**
   * Get interrupt status.
   * 
   * @return Interrupt status of this thread, <code>true</code> if interrupted,
   *         <code>false</code> otherwise.
   */
  public boolean getInterruptStatus() {
    return _interruptTime != 0;
  }

  /**
   * Enable the possibility of spurious wakeups.
   */
  public void enableSpuriousWakeups() {
    _spuriousWakeupTime = 0;
  }

  /**
   * Trigger spurious wakeup. The request is ignored if the thread is not in a
   * blocked state or if the spurious wakeup status is already set.
   */
  public void triggerSpuriousWakeup() {
    if (_spuriousWakeupTime == 0) {
      _spuriousWakeupTime = newEventTime();
    }
  }

  /**
   * Get textual representation of the thread state. The method is used for
   * debugging purposes.
   * 
   * @return A string object.
   */
  @Override
  public String toString() {
    return String.format("[%03d,O=%s,Y=%s,S=%s,J=%s,Y=%s,I=%s,D=%s]", _cid,
        _operation, _location, getCState(), getState(), _atYieldPoint,
        _interruptTime, _dying);
  }

  @Override
  protected void finalize() {
    assert CWorkspace.debug(_cid + " collected by GC");
  }




}
