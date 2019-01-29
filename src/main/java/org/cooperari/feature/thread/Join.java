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

package org.cooperari.feature.thread;

import static org.cooperari.core.CThreadState.CREADY;
import static org.cooperari.core.CThreadState.CTERMINATED;
import static org.cooperari.core.CThreadState.CTIMED_WAITING;
import static org.cooperari.core.CThreadState.CWAITING;

import org.cooperari.core.CThread;
import org.cooperari.core.CThreadState;

/**
 * Cooperative operation for {@link Thread#join()}, {@link Thread#join(long)} and {@link Thread#join(long, int)}.
 * 
 * @since 0.2
 *
 */
public final class Join extends ThreadOperation<Boolean> {

  /**
   * This thread.
   */
  private final CThread _thisThread;
  
  /**
   * Thread to join.
   */
  private final CThread _joinThread;
  
  /**
   * Join deadline in nanoseconds ({@link System#nanoTime()} wall time).
   */
  private final long _deadline;

  /**
   * Interrupt flag. 
   */
  private boolean _interrupted;
  
  /**
   * Constructor.
   * @param thisThread Thread to join.
   * @param joinThread Thread to join.
   * @param timeout Join timeout in nanoseconds.
   */ 
  Join(CThread thisThread, CThread joinThread, long timeout) {
    super(joinThread, timeout);
    _thisThread = thisThread;
    _joinThread = joinThread;
    _deadline = timeout <= 0L ? timeout : System.nanoTime() + timeout;
    _interrupted = false;
  }

  /**
   * Get state. 
   * @return {@link CThreadState#CREADY}, {@link CThreadState#CWAITING}, or {@link CThreadState#CTIMED_WAITING} in line
   * with the semantics of {@link Thread#join()} and its timed variants.
   */
  @Override
  public CThreadState getState() {
    // TODO happens-before ?? interrupt + joinThread termination
    if (_deadline < 0L || _joinThread == null || _interrupted) {
      return CREADY; 
    }
    
    if (_thisThread.testAndClearInterruptStatus()) {
      _interrupted = true;
      return CREADY;
    } 
    
    if (_thisThread != _joinThread && _joinThread.getCState() == CTERMINATED) {
      return CREADY;
    }
    
    if (_deadline == 0L) {
      return CWAITING;
    }
    return System.nanoTime() - _deadline >= 0 ? CREADY : CTIMED_WAITING;
  }

  /**
   * Complete operation.
   */
  @Override
  public void execute() {
    if (_joinThread == null) {
      throw new NullPointerException();
    }
    if (_deadline < 0L) {
      throw new IllegalArgumentException("Invalid join timeout argument.");
    }
  }
  
  /**
   * Get completion result.
   * @return <code>false</code> for normal completion, <code>true</code> if interrupted.
   */
  @Override 
  public Boolean getResult() {
    return _interrupted;
  }

  /**
   * Execute a thread join operation.
   * @param thisThread This thread.
   * @param joinThread Thread to join.
   * @param timeout Join timeout in nanoseconds: <code>-1L</code> if invalid, <code>0L</code> for no timeout, 
   * a positive value for a timed join.
   * @throws IllegalArgumentException In accordance to {@link Thread#join(long)} and {@link Thread#join(long, int)}.
   * @throws InterruptedException In accordance to {@link Thread#join()}, {@link Thread#join(long)} and {@link Thread#join(long, int)}.
   */
  public static void execute(CThread thisThread, Thread joinThread, long timeout) throws InterruptedException {
    if ( thisThread.cYield(new Join(thisThread, CThreadFeature.getCThread(joinThread), timeout)) ) {
      throw new InterruptedException();
    }
  }
}
