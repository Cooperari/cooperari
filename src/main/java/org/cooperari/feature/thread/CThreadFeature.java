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
