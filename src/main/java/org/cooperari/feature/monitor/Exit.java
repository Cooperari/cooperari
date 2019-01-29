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

import static org.cooperari.core.CRuntime.getRuntime;

import org.cooperari.core.CThread;

/**
 * Monitor exit operation (lock release).
 * 
 * @since 0.2
 *
 */
final class Exit extends MonitorOperation<Void> {    

  /**
   * Constructor.
   * @param t thread
   * @param m Monitor.
   */  
  private Exit(CThread t, Monitor m) {
    super(t, m);
  }

  /**
   * Execute operation by releasing the lock.
   */
  @Override
  public void execute() {
    _monitor.exit();
    if (_monitor.getOwner() != _thread) {
      DeadlockDetector dd = getRuntime().get(DeadlockDetector.class);
      if (dd != null) {
        dd.onMonitorExit(_thread);
      }
    }
  }


  /**
   * Execute operation.
   * @param t This thread.
   * @param o Object being unlocked.
   */
  public static void execute(CThread t, Object o) {
    if (o != null) {
      MonitorPool ms = getRuntime().get(MonitorPool.class);
      Monitor m = ms.get(o);
      t.cYield(new Exit(t, m)); 
      ms.release(m); 
    }
  }
}
