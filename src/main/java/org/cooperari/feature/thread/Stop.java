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
 * Cooperative operation for {@link Thread#stop()}.
 * 
 * @since 0.2
 */
public  final class Stop extends ThreadOperation<Void> {   

  /**
   * This thread. 
   */
  private final CThread _thisThread;
  /**
   * Thread to stop.
   */
  private final CThread _threadToStop;

  
  /**
   * Constructor.
   * @param thisThread Current thread.
   * @param threadToStop Thread to stop.
   */ 
  private Stop(CThread thisThread, CThread threadToStop) {
    super(threadToStop);
    _thisThread = thisThread;
    _threadToStop = threadToStop;
  }
  

  /**
   * Execute by initiating the stop process for the other thread.
   */
  @Override
  public void execute() {
    ThreadDeath death = new ThreadDeath();
    if (_threadToStop == _thisThread) {
      throw death;
    } else {
      _threadToStop.cStop(death);
    }
  }

  /**
   * Execute a thread stop operation.
   * @param thisThread This thread.
   * @param otherThread Thread to stop.
   */
  public static void execute(CThread thisThread, Thread otherThread) {
    thisThread.cYield(new Stop(thisThread, CThreadFeature.getCThread(otherThread)));
  }
}
