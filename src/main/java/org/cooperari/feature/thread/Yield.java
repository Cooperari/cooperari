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
 * Cooperative operation for {@link Thread#yield()}.
 * 
 * @since 0.2
 */
public  final class Yield extends ThreadOperation<Void> {    
  /**
   * Constructor.
   */ 
  private Yield() {
    super();
  }
  
  /**
   * Singleton instance.
   */
  public final static Yield INSTANCE = new Yield();


  /**
   * Execute a thread yield operation.
   * @param thisThread This thread.
   */
  public static void execute(CThread thisThread) {
    thisThread.cYield(INSTANCE);
  }
}
