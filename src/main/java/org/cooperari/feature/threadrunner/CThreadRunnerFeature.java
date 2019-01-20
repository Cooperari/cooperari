package org.cooperari.feature.threadrunner;

import org.cooperari.feature.CFeature;

/**
 * Thread runner feature (support for {@link org.cooperari.CSystem#forkAndJoin}).
 *
 * @since 0.2
 */
public final class CThreadRunnerFeature implements CFeature {  
  
  @Override
  public Class<?> getInstrumentationAspect() {
    return YieldPoints.class;
  }
  
}
