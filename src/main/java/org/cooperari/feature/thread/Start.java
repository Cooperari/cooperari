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

import org.cooperari.core.CThread;

/**
 * Cooperative operation for {@link Thread#start()}.
 * 
 * @since 0.2
 *
 */
public final class Start extends ThreadOperation<Void> {
  /**
   * This thread.
   */
  private final CThread _thisThread;
  
  /**
   * Thread to start.
   */
  private final Thread _threadToStart;

  /**
   * Constructor.
   * @param thisThread Current thread.
   * @param threadToStart Thread to start.
   */ 
  private Start(CThread thisThread, Thread threadToStart) {
    super();
    _thisThread = thisThread;
    _threadToStart = threadToStart;
  }

 
  /**
   * Complete operation.
   * Clear the interrupted status of the thread if necessary in line
   * with the contract of <code>Thread.sleep()</code>.
   */
  @Override
  public void execute() {
    if (_threadToStart.getState() != Thread.State.NEW) {
      throw new IllegalThreadStateException("Thread already started");
    }
    _thisThread.getEngine().createNewThread(_threadToStart, null);
  }


  /**
   * Execute a thread start operation.
   * @param thisThread This thread.
   * @param targetThread Thread to start.
   * @throws IllegalThreadStateException In the conditions stated for {@link Thread#start()}.
   */
  public static void execute(CThread thisThread, Thread targetThread) throws IllegalThreadStateException {
    thisThread.cYield(new Start(thisThread, targetThread));
  }
}
