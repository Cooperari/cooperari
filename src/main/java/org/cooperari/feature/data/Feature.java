package org.cooperari.feature.data;

import org.cooperari.config.CRaceDetection;
import org.cooperari.core.CRuntime;
import org.cooperari.core.FeatureHandler;


/**
 * Handler for data access yield points.
 *
 * @since 0.2
 */
public final class Feature implements FeatureHandler {

  /**
   * @{inheritDoc}
   */
  @Override
  public void init(CRuntime env) {
    CRaceDetection config = env.getConfiguration(CRaceDetection.class);
    if (config.value()) {
      env.register(new RaceDetector(config));
    } 
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public void shutdown(CRuntime env) {
    if (env.get(RaceDetector.class) != null) {
      env.unregister(RaceDetector.class);
    }
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public Class<?> getInstrumentationAspect() {
    return YieldPoints.class;
  }

}
