package org.cooperari.core;

import java.util.List;

/**
 * Feature handler interface.
 * 
 * <p>
 * A feature handler is responsible for reporting configuration settings and handling life-cycle actions
 * for a Cooperari feature.
 * </p>
 * 
 * @since 0.2
 * @see org.cooperari.core.Features
 */

public interface FeatureHandler {
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
}
