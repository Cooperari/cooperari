//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.cooperari.core;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.cooperari.config.CScheduling;
import org.cooperari.core.scheduling.CProgramStateFactory;
import org.cooperari.core.scheduling.CScheduler;
import org.cooperari.errors.CInternalError;
import org.cooperari.errors.CWaitDeadlockError;


/**
 * Cooperative execution engine.
 * 
 * @since 0.2
 *
 */
public class CEngine extends Thread {

  /**
   * Runtime instance.
   */
  private final CRuntime _runtime;

  /**
   * Internal lock for waking up the scheduler.
   */
  private final Object _wakeupLock = new Object();

  /**
   * Scheduler.
   */
  private final CScheduler _scheduler;

  /**
   *  Map representing all  threads.
   *  Each thread has an unique cooperative identifier that is used as a key to this map
   *  by the {@link #getThread(int)} utility method.
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
   * @param scheduler Coverage policy.
   * @param runnables Array of {@link Runnable} instances. The scheduler thread will create one initial thread per each element in the array.
   */
  public CEngine(CRuntime runtime, CScheduler scheduler, Runnable... runnables) {
    super("CSCheduler");
    _runtime = runtime;
    _scheduler = scheduler;
    _runtime.register(this);
    _runtime.register(new CThreadMappings());
    _trace = _runtime.get(CTrace.class);
    setUncaughtExceptionHandler(_uncaughtExceptionHandler);

    for (Runnable r : runnables) {
      createNewThread(r, null);
    }
  }

  /**
   * Throw any uncaught exceptions during thread execution. 
   * <p>
   * An {@link CUncaughtExceptionHandler} object is used internally to record
   * uncaught exceptions and the engine calls {@link CUncaughtExceptionHandler#rethrowExceptionsIfAny()} on that object
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
   * Tell the engine to start a new thread.
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
   * Execution method for the engine thread.
   */
  @Override
  public void run() {
    // Setup stage
    assert CWorkspace.debug("Scheduler started.");
    _runtime.join();

    handleNewThreads(); // handle initial thread

    ArrayList<CThread> readyThreads = new ArrayList<>();
    ArrayList<CThread> blockedThreads = new ArrayList<>();
    CProgramStateFactory stateFac = _runtime.getConfiguration(CScheduling.class).stateFactory();
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
        // Running thread either (1) yielded or (2) terminated
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
        readyThreads.clear();
        blockedThreads.clear();
        int cannotProgressCount = 0;
        for (CThread t : _threads.values()) {
          assert CWorkspace.debug(t.toString());
          CThreadState s = t.getCState();
          switch (s) {
            case CREADY:
              readyThreads.add(t); 
              break;
            case CBLOCKED:
            case CWAITING:
              cannotProgressCount ++;
            case CTIMED_WAITING:
              blockedThreads.add(t);
              break;
            default:
              throw new CInternalError("Unexpected thread state: " + t.getName() + " -> " + s + " -- " + t.getLocation());
          }
        }

        //assert debug("A = %d B = %d W =%d", aliveCount, blockedCount, waitCount);
        if (cannotProgressCount == _threads.size()) {
          CWaitDeadlockError e = new CWaitDeadlockError(_threads.values());
          for (CThread t : _threads.values()) {
            assert CWorkspace.debug("Stopping "+ t.getName());
            _trace.record(t, CTrace.EventType.DEADLOCK);
            t.cStop(e); 
          }
        }

        if (readyThreads.size() > 0) {
          running = (CThread) _scheduler.decision(stateFac.create(readyThreads, blockedThreads));
          if (running == null || !running.isReady()) {
            throw new CInternalError("Scheduler made a wrong decision!");
          }
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
