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

package org.cooperari.feature;

import java.util.List;

import org.cooperari.core.CRuntime;
import org.cooperari.core.CustomYieldPoint;

/**
 * Feature handler interface.
 * 
 * <p>
 * A feature handler is responsible for reporting configuration settings and handling life-cycle actions
 * for a Cooperari feature.
 * </p>
 * 
 * @since 0.2
 * @see org.cooperari.feature.CAllFeatures
 */

public interface CFeature {
  /**
   * Initialize the feature.
   * <p>
   * Any environment initialization actions, like shared object registration, should be executed by this method.
   * The default implementation does nothing.
   * </p>
   * 
   * @param env A {@link CRuntime} object.
   */
  default void init (CRuntime env) {
    
  }
  /**
   * Shutdown the feature.
   * <p>
   * Any environment cleanup actions, like shared object unregistration, should be executed by this method.
   * The default implementation does nothing.
   * </p>
   * 
   * @param env A {@link CRuntime} object.
   *
   */
  default void shutdown(CRuntime env) {
    
  }
  /**
   * Get aspect class for feature.
   * 
   * <p>
   * The default implementation returns <code>null</code>, in that case implying that the feature does not require 
   * AspectJ-based instrumentation.
   * </p>
   * 
   * @return A {@link Class} object that should be annotated with the {@link org.aspectj.lang.annotation.Aspect}
   * annotation, or <code>null</code> if the feature does not require instrumentation.
   * 
   */
  default Class<?> getInstrumentationAspect() {
    return null;
  }
  
  /**
   * Add custom yield points to given list.
   * <p>
   * The default implementation does  nothing, in that case implying that the feature does not use 
   * custom yield points.
   * </p>
   * @param list A list object. 
   * 
   * @see org.cooperari.core.CustomYieldPoint
   */
  default void getCustomYieldPoints(List<CustomYieldPoint> list) {

  }
  
  /**
   * Indicates if the feature should be activated only
   * when cooperative semantics are enabled.
   * 
   * @return <code>true</code> by default.
   */
  default boolean cooperativeSemanticsRequired() {
    return true;
  }
}
