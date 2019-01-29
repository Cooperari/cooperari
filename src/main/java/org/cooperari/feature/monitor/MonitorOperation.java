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

import org.cooperari.core.COperation;
import org.cooperari.core.CThread;

/**
 * Base class for monitor operations.
 * 
 * @since 0.2
 *
 * @param <T> Type for operation result.
 */
abstract class MonitorOperation<T> extends COperation<T> {
  /**
   * Thread.
   */
  protected final CThread _thread;

  /**
   * Monitor.
   */
  protected final Monitor _monitor;

  /**
   * Constructor.
   * @param t thread
   * @param m Monitor.
   */ 
  protected MonitorOperation(CThread t, Monitor m) {
    super(m);
    _thread = t;
    _monitor = m;
  }

}
