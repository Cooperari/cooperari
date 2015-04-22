package org.cooperari.feature.threadrunner;

import org.cooperari.core.FeatureHandler;

/**
 * Thread runner feature (support for {@link org.cooperari.CSystem#forkAndJoin}).
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
  
}
