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
 * Cooperative operation for {@link Thread#interrupted()}.
 * 
 * @since 0.2
 */
public final class Interrupted extends ThreadOperation<Boolean> {
  /**
   * Thread to query interruption.
   */
  private final CThread _thread;

  /**
   * Result after completion.
   */
  private boolean _result;

  /**
   * Constructor.
   * @param thread Thread to interrupt.
   */ 
  Interrupted(CThread thread) {
    super();
    _thread = thread;
    _result = false;
  }

  /**
   * Complete by clearing the interrupt status of the thread
   * and getting previous status.
   */
  @Override
  public void execute() {
    _result = _thread.testAndClearInterruptStatus();
  }

  /**
   * Get result, i.e., the previous interrupted status for the thread.
   * @return A <code>Boolean</code> object.
   */
  public Boolean getResult() {
    return _result;
  }

  /**
   * Execute an operation of this kind.
   * @param t Thread to interrupt (should be the current thread always).
   * @return Interruption status.
   */
  public static boolean execute(CThread t) {
    // Note: t will be the current thread.
    return t.cYield(new Interrupted(t));
  }
}
