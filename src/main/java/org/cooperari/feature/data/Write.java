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

package org.cooperari.feature.data;

import static org.cooperari.core.CRuntime.getRuntime;

import org.cooperari.core.CThread;

/**
 * Data write operation.
 * @author Eduardo Marques, DI/FCUL, 2014-15
 */
public final class Write extends DataOperation {

  /**
   * Constructs the operation.
   * @param object Object.
   * @param key Data key.
   */
  public Write(Object object, Object key) {
    super(object, key);
    if (object != null) {
      RaceDetector rd = getRuntime().get(RaceDetector.class);
      if (rd != null) {
        rd.beginWrite(object, key);
      }
    }
  }

  /**
   * Yield on data write.
   * @param thisThread Current thread.
   * @param object Object.
   * @param key Data key.
   */
  public static void before(CThread thisThread, Object object, Object key) {
    thisThread.cYield(new Write(object, key));
  }
  
  /**
   * Execute actions after data write without yielding.
   * @param thisThread Current thread.
   * @param object Object.
   * @param key Data key.
   */
  public static void after(CThread thisThread, Object object, Object key) {
    RaceDetector rd = getRuntime().get(RaceDetector.class);
    if (rd != null) {
      rd.endWrite(object, key);
    }
  }
}
