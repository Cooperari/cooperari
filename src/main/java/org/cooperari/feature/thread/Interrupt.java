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
 * Cooperative operation for {@link Thread#interrupt()}.
 * 
 * @see Thread#interrupt()
 */
public final class Interrupt extends ThreadOperation<Void> {
  /**
   * Thread to interrupt.
   */
  private final CThread _targetThread;
  
  /**
   * Constructor.
   * @param targetThread Thread to interrupt.
   */ 
  private Interrupt(CThread targetThread) {
    super(targetThread);
    _targetThread = targetThread;
  }
 
  /**
   * Complete by interrupting the thread.
   */
  @Override
  public void execute() {
    _targetThread.triggerInterrupt();
  }
  
  /**
   * Execute thread interrupt operation.
   * @param thisThread This thread.
   * @param targetThread Thread to interrupt.
   */
  public static void execute(CThread thisThread, Thread targetThread) {
    thisThread.cYield(new Interrupt(CThreadFeature.getCThread(targetThread)));
  }
}