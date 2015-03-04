package org.cooperari.feature.thread;

import org.cooperari.core.CRuntime;
import org.cooperari.core.CThread;
import org.cooperari.core.FeatureHandler;
import org.cooperari.core.ThreadMappings;

/**
 * Thread life-cycle yield points feature.
 *
 * @since 0.2
 */
public final class Feature implements FeatureHandler {  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getInstrumentationAspect() {
    return YieldPoints.class;
  }
  
  /**
   * Shortcut method that invokes {@link ThreadMappings#getCThread(Thread)} on the thread mappings object
   * registered for the current runtime.
   * @param t Thread
   * @return A {@link CThread} instance.
   */
  static CThread getCThread(Thread t) {
    return CRuntime.getRuntime().get(ThreadMappings.class).getCThread(t);
  }
}
