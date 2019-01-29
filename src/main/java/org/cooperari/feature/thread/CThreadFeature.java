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

import org.cooperari.core.CRuntime;
import org.cooperari.core.CThread;
import org.cooperari.core.CThreadMappings;
import org.cooperari.feature.CFeature;

/**
 * Thread life-cycle yield points feature.
 *
 * @since 0.2
 */
public final class CThreadFeature implements CFeature {  
  
  @Override
  public Class<?> getInstrumentationAspect() {
    return YieldPoints.class;
  }
  
  /**
   * Shortcut method that invokes {@link CThreadMappings#getCThread(Thread)} on the thread mappings object
   * registered for the current runtime.
   * @param t Thread
   * @return A {@link CThread} instance.
   */
  static CThread getCThread(Thread t) {
    return CRuntime.getRuntime().get(CThreadMappings.class).getCThread(t);
  }
}
