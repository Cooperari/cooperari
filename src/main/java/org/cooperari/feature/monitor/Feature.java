package org.cooperari.feature.monitor;

import org.cooperari.CDetectResourceDeadlocks;
import org.cooperari.core.CRuntime;
import org.cooperari.core.FeatureHandler;

/**
 * Handler for monitor yield points.
 *
 * @since 0.2
 */
public final class Feature implements FeatureHandler {

  /**
   * @{inheritDoc}
   */
  @Override
  public void init(CRuntime env) {
    env.register(new MonitorPool());
    CDetectResourceDeadlocks config = env.getConfiguration(CDetectResourceDeadlocks.class);
    env.register(new DeadlockDetector(config));
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public void shutdown(CRuntime env) {
    env.unregister(MonitorPool.class);
    env.unregister(DeadlockDetector.class);
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public Class<?> getInstrumentationAspect() {
    return YieldPoints.class;
  }

}
