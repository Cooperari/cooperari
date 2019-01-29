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
 * Operation for {@link Object#notify()}.
 * 
 * @since 0.2
 */
public final class Notify extends MonitorOperation<Void> {

  /**
   * Constructor.
   * @param t Thread.
   * @param m Monitor
   */
  Notify(CThread t, Monitor m) {
    super(t, m);
  }


  @Override
  public void execute() {
    if (_monitor == Monitor.NULL) {
      throw new NullPointerException();
    }
    if (_monitor.getOwner() != _thread) {
      throw new IllegalMonitorStateException("Monitor is not owned by current thread.");
    }
    _monitor.notifyOneThread();
  }

  /**
   * Execute operation.
   * @param t Current thread.
   * @param o Target object.
   * @throws IllegalMonitorStateException As described for <code>Object.notifyAll</code>.
   */
  public static void execute(CThread t, Object o) throws IllegalMonitorStateException  {
    MonitorPool ms = getRuntime().get(MonitorPool.class);
    Monitor m = ms.get(o);
    t.cYield(new Notify(t, m));
  }
}