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

package org.cooperari.feature.monitor;


import static org.cooperari.core.CBlockingOperationEvent.INTERRUPTION_EVENT;
import static org.cooperari.core.CBlockingOperationEvent.TIMEOUT_EVENT;
import static org.cooperari.core.CRuntime.getRuntime;
import static org.cooperari.core.CThreadState.CBLOCKED;
import static org.cooperari.core.CThreadState.CREADY;
import static org.cooperari.core.CThreadState.CTIMED_WAITING;
import static org.cooperari.core.CThreadState.CWAITING;

import org.cooperari.core.CBlockingOperationEvent;
import org.cooperari.core.COperation;
import org.cooperari.core.CThread;
import org.cooperari.core.CThreadState;



/**
 * Operations for <code>Object.wait()</code>.
 * @author Eduardo Marques, DI/FCUL, 2014-15
 */
public final class Wait {

  /**
   * Private constructor to avoid unintended construction.
   */
  private Wait() { }


  /**
   * Operation for the ownership release step.
   */
  private static final class ReleaseOwnership extends MonitorOperation<Integer>{
    /**
     * Result. 
     */
    private int _result = -1;

    /**
     * Constructor.
     * @param t Thread.
     * @param m Monitor
     */
    ReleaseOwnership(CThread t, Monitor m) {
      super(t, m);
    }

    /**
     * Complete operation.
     */
    @Override
    public void execute() {
      if (_monitor == Monitor.NULL) {
        throw new NullPointerException();
      }
      if (_monitor.getOwner() != _thread) {
        throw new IllegalMonitorStateException("Monitor is not owned by current thread.");
      }
      _result = _monitor.relinquish(_thread, true);
    }

    /**
     * Get operation result.
     * @return The lock count before relinquishing.
     */
    @Override
    public
    Integer getResult() {
      return _result;
    }
  }

  /**
   * Operation for notification step.
   */
  private static final class AwaitNotification extends MonitorOperation<CBlockingOperationEvent>{
    /**
     * Notification epoch.
     */
    private final int _epoch;
    /**
     * Deadline. 
     */
    private final long _deadline;
    /**
     * Lock count.
     */
    private final int _lockCount;

    /**
     * Possible event during operation.
     */
    private CBlockingOperationEvent _event = null;

    /**
     * Constructor.
     * @param t Thread.
     * @param m Monitor
     * @param timeout Timeout in nanoseconds.
     * @param lockCount Lock count.
     */
    AwaitNotification(CThread t, Monitor m, long timeout, int lockCount) {
      super(t, m);
      _epoch = m.getNotifyEpoch();
      _deadline = timeout > 0L ? System.nanoTime() + timeout : 0L;
      _lockCount = lockCount;
      t.enableSpuriousWakeups();
    }

    /**
     * Get operation state.
     * @return {@link CThreadState#CWAITING} or {@link CThreadState#CTIMED_WAITING} (for time-based variants)
     * until either: (1) notification is available, (2) deadline is set and expires; (3) interrupt status for the thread is set, or (4)
     * a simulated spurious wakeup is received by the thread. The state will be {@link CThreadState#CREADY}
     * in cases (1)-(4).
     */
    @Override
    public CThreadState getState() {
      if (_event != null || _monitor.availableNotification(_epoch)) {
        return CREADY;
      }
      CBlockingOperationEvent e = _thread.testAndClearWakeupEvents();
      if (e != null) {
        _event = e;
        return CREADY;
      }
      if (_deadline > 0 ) {
        if ( System.nanoTime() - _deadline >= 0 ) {
          _event = TIMEOUT_EVENT;
          return CREADY;
        } 
        return CTIMED_WAITING;
      }  
      return CWAITING;
    }

    /**
     * Get abort operation.
     * The abort operation will re-acquire the lock.
     * @return A {@link RegainOwnership} operation.
     */
    @Override
    public COperation<Void> getAbortOperation() {
      return new RegainOwnership(_thread, _monitor, _lockCount);
    }

    /**
     * Get yield point stage.
     * @return <code>1</code>
     */
    @Override
    public int getStage() {
      return 1;
    }

    /**
     * Complete operation.
     */
    @Override
    public void execute() {
      if (_event == null) {
        // This means we can consume a notification.
        _monitor.consumeNotification();
      }
    }
    
    /**
     * Get operation result. 
     * @return <code>null</code> if notification was received, the completion event otherwise.
     */
    @Override 
    public CBlockingOperationEvent getResult() {
      return _event;
    }
  }

  /**
   * Operation for the ownership re-acquisition step.
   */
  private static final class RegainOwnership extends MonitorOperation<Void>{
    /**
     * Lock count for monitor re-acquisition. 
     */
    private final int _lockCount;

    /**
     * Constructor.
     * @param t Thread.
     * @param m Monitor
     * @param lockCount Lock count on ownership release.
     */
    RegainOwnership(CThread t, Monitor m, int lockCount) {
      super(t, m);
      _lockCount = lockCount;
    }

    /**
     * Get operation state.
     * Blocked state will be indicated while monitor is unlocked.
     * @return <code>CREADY</code> when ready,  <code>CBLOCKED</code> otherwise.
     */
    @Override
    public CThreadState getState() {
      return _monitor.isLocked() ? CBLOCKED : CREADY;
    }
    /**
     * Get yield point stage.
     * @return <code>2</code>
     */
    @Override
    public int getStage() {
      return 2;
    }
    /**
     * Complete by re-acquiring the lock.
     */
    @Override
    public void execute() {
      _monitor.reacquire(_thread, _lockCount);
    }
  }

  /**
   * Execute a wait on the monitor. 
   * <p>
   * This comprises the following three yield points in order, all of them implemented through distinct operations:
   * <ol>
   * <li>Release ownership for the monitor.</li>
   * <li>Wait until one of the following happens:
   * <ul>
   * <li>A monitor notification is available and consume by the thread;</li>
   * <li>If the wait deadline is set expires;</li>
   * <li>The thread receives an interrupt;</li>
   * <li>The thread receives a (simulated) spurious wakeup;</li>
   * </ul>
   * <li>Regain ownership for the monitor</li>
   * </ol>
   * @param t Current thread.
   * @param o Target object.
   * @param timeout Timeout in nanoseconds.
   * @throws IllegalMonitorStateException In accordance to the contract of <code>Object.wait()</code>.
   * @throws InterruptedException In accordance to the contract of <code>Object.wait()</code>.
   */
  public static void execute(CThread t, Object o, long timeout) throws IllegalMonitorStateException, InterruptedException {
    MonitorPool ms = getRuntime().get(MonitorPool.class);
    Monitor m = ms.get(o);
    // Release ownership
    int lockCount = t.cYield(new ReleaseOwnership(t, m));
    
    // Await notification.
    CBlockingOperationEvent e = t.cYield(new AwaitNotification(t, m, timeout, lockCount));
    
    // Regain ownership.
    t.cYield(new RegainOwnership(t, m, lockCount));
    
    if (e == INTERRUPTION_EVENT) {
      // If interrupted.
      throw new InterruptedException();
    }
  }
}
