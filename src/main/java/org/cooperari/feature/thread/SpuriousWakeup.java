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
public  final class SpuriousWakeup extends ThreadOperation<Void> {    
 
  /**
   * Target thread.
   */
  private final CThread _targetThread;
  
  /**
   * Constructor.
   * @param targetThread Target thread.
   */ 
  private SpuriousWakeup(CThread targetThread) {
    _targetThread = targetThread;
  }
  
  /**
   * Execute operation
   */
  @Override
  public void execute() {
    _targetThread.triggerSpuriousWakeup();
  }


  /**
   * Execute spurious wakeup operation.
   * @param thisThread This thread.
   * @param targetThread Target thread for spurious wakeup.
   */
  public static void execute(CThread thisThread, Thread targetThread) {
    thisThread.cYield(new SpuriousWakeup(CThreadFeature.getCThread(targetThread)));
  }
}
