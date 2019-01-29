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
 * Operation for sending a (simulated) spurious wakeup to another thread.
 * 
 * @since 0.2
 *
 */
public  final class GetState {    
 

  /**
   * Execute {@link Thread#getState()} operation.
   * @param thisThread This thread.
   * @param targetThread Target thread.
   * @return The state of the target thread.
   */
  public static Thread.State execute(CThread thisThread, Thread targetThread) {
    thisThread.cYield(CThread.NOP);
    return  CThreadFeature.getCThread(targetThread).getVirtualState();
  }
}
