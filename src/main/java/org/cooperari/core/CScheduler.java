package org.cooperari.core;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.cooperari.errors.CInternalError;


/**
 * Cooperative scheduler.
 * 
 * @since 0.2
 *
 */
public class CScheduler extends Thread {

  /**
   * Runtime instance.
   */
  private final CRuntime _runtime;

  /**
   * Internal lock for waking up the scheduler.
   */
  private final Object _wakeupLock = new Object();

  /**
   * Coverage policy.
   */
  private final CoveragePolicy _coveragePolicy;

  /**
   *  Map representing all alive threads.
   *  Each thread has an unique cooperative identifier that is used as a key to this map
   *  for the sole purpose of serving the {@link #getThread(int)} utility method.
   *  @see #_cid
   *  @see #_newThreads
   */
  private final LinkedHashMap<Integer,CThread> _threads = new LinkedHashMap<>();

  /**
   * Cooperative thread id counter. It gets incremented every time a new thread is created.
   * @see #_newThreads
   */
  private int _cid = 0;

  /**
   * Fresh threads waiting to be started.
   */
  private final ArrayList<CThread> _newThreads = new ArrayList<>();

  /**
   * Execution trace.
   */
  private final CTrace _trace;

  /**
   * Virtual preemption count.
   */
  private long _virtualPreemptions = 0;

  /**
   * Scheduling steps.
   */
  private long _schedulingSteps = 0;


  /**
   * Handler for uncaught exceptions thrown by threads executing in this scheduler.
   */
  private CUncaughtExceptionHandler _uncaughtExceptionHandler = new CUncaughtExceptionHandler();


  /**
   * Constructs a new cooperative scheduler.
   * @param runtime Runtime environent instance.
   * @param coverage Coverage policy.
   * @param runnables Array of {@link Runnable} instances. The scheduler thread will create one initial thread per each element in the array.
   */
  public CScheduler(CRuntime runtime, CoveragePolicy coverage, Runnable... runnables) {
    super("CSCheduler");
    _runtime = runtime;
    _coveragePolicy = coverage;
    _runtime.register(this);
    _runtime.register(new ThreadMappings());
    _trace = _runtime.get(CTrace.class);
    setUncaughtExceptionHandler(_uncaughtExceptionHandler);

    for (Runnable r : runnables) {
      createNewThread(r, null);
    }
  }

  /**
   * Throw any uncaught exceptions by threads executed using this scheduler. 
   * <p>
   * The scheduler uses a {@link CUncaughtExceptionHandler} object internally to record
   * uncaught exceptions and calls {@link CUncaughtExceptionHandler#rethrowExceptionsIfAny()} on that object
   * at this point.
   * </p>
   * @see CUncaughtExceptionHandler#rethrowExceptionsIfAny()
   */
  public void rethrowExceptionsIfAny() {
    _uncaughtExceptionHandler.rethrowExceptionsIfAny();
  }


  /**
   * Get {@link CRuntime} object used by the scheduler.
   * @return The  {@link CRuntime} object used by the scheduler.
   */
  public CRuntime getRuntime() {
    return _runtime;
  }


  /**
   * Tell the scheduler to start a new thread.
   * @param r A {@link Runnable} instance to be used by the new thread.
   * @param excHandler Custom exception handler (internal scheduler handler used otherwise).
   * @return The {@link CThread} object that was created. On exit the thread will not have been started.
   */
  public CThread createNewThread(Runnable r, CUncaughtExceptionHandler excHandler) {
    CThread t = new CThread(this, r,  _cid);
    _cid++;
    t.setUncaughtExceptionHandler(excHandler != null ? excHandler : _uncaughtExceptionHandler);
    assert CWorkspace.debug("Created new thread %d", t.getCID());
    synchronized( _newThreads) {
      _newThreads.add(t);
    }
    return t;
  }


  /**
   * Wake up scheduler in case it is dormant.
   */
  public void wakeup() {
    synchronized (_wakeupLock) {
      _wakeupLock.notify();
    }
  }

  /**
   * Gets thread created by the scheduler by creation order index.
   * <p>
   * The method will return the <code>index</code>-th thread created by scheduler,
   * if that thread is still alive.
   * </p>
   * @param index Index.
   * @return A {@link CThread} object if the index is valid and the identified thread is still alive.
   * @throws IllegalArgumentException if the index is invalid or no longer refers to an active thread.
   */
  public CThread getThread(int index) {
    CThread t = _threads.get(index);
    if (t == null) {
      throw new IllegalArgumentException("Invalid index: " + index);
    }
    return t;
  }



  /**
   * Execution method for the scheduler.
   */
  @Override
  public void run() {
    // Setup stage
    assert CWorkspace.debug("Scheduler started.");
    _runtime.join();
    
    handleNewThreads(); // handle initial thread

    ArrayList<CThread> readySet = new ArrayList<>();

    CThread running = null, lastRunning = null;

    while (_threads.size() > 0) {
      synchronized (_wakeupLock) {
        try {
          _wakeupLock.wait(1);
        } catch (InterruptedException e) {
          throw new CInternalError(e);
        }
      }
      if (running != null && !running.isRunning()) {
        // Running thread either (1) blocked or (2) terminated
        _trace.recordStep(running);
        handleNewThreads();
        if (running.isTerminated()) {
          _threads.remove(running.getCID());
          assert CWorkspace.debug("Thread %d terminated execution [%d threads left].", running.getCID(), _threads.size());
        }
        lastRunning = running;
        running = null;
      }

      if (running == null && _threads.size() > 0) {
   
        readySet.clear();
        int waitCount = 0, aliveCount = _threads.size(), blockedCount = 0;
        for (CThread t : _threads.values()) {
          assert CWorkspace.debug(t.toString());
          switch (t.getCState()) {
            case CTERMINATED:
              aliveCount--; break;
            case CREADY:
              readySet.add(t); break;
            case CBLOCKED:
              blockedCount ++; break;
            case CWAITING:
              waitCount ++; break;
            default:
              break;
          }
        }
        //assert debug("A = %d B = %d W =%d", aliveCount, blockedCount, waitCount);
        if (aliveCount > 0 && waitCount + blockedCount == aliveCount) {
          WaitDeadlockError e = new WaitDeadlockError(_threads.values());
          for (CThread t : _threads.values()) {
            if (t.isWaiting() || t.isBlocked()) {
              assert CWorkspace.debug("Stopping "+ t.getName());
              _trace.record(t, CTrace.EventType.DEADLOCK);
              t.cStop(e); 
            }
          }
        }

        if (readySet.size() > 0) {
          running = _coveragePolicy.decision(readySet);
          if (running != lastRunning)
            _virtualPreemptions++;
          _schedulingSteps++;
          assert CWorkspace.debug("%s will now run", running.getCID());
          running.cResume();
        }
      }
    }
    _runtime.leave();
    assert CWorkspace.debug("Done! Context switches: %d; Scheduling steps: %d.", 
        _virtualPreemptions, 
        _schedulingSteps); 
  }


  @SuppressWarnings("javadoc")
  private void handleNewThreads() {
    // Check for new threads.
    synchronized (_newThreads) {
      Iterator<CThread> itr = _newThreads.iterator();
      while(itr.hasNext()) {
        CThread t = itr.next();
        itr.remove();
        assert CWorkspace.debug("Thread %d will be started now.", t.getCID());
        t.start();
        while ( ! t.atYieldPoint()) {
          try { 
            Thread.sleep(1);
          } 
          catch (InterruptedException e) {
            throw new CInternalError(e);
          }
        }
        _threads.put(t.getCID(), t);
        _trace.recordThread(t);
        _trace.recordStep(t);
        assert CWorkspace.debug("Thread %d is now ready.", t.getCID());
      }
    }
  }


}
