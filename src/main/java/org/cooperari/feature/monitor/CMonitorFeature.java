package org.cooperari.feature.monitor;

import org.cooperari.config.CDetectResourceDeadlocks;
import org.cooperari.core.CRuntime;
import org.cooperari.feature.CFeature;

/**
 * Handler for monitor yield points.
 *
 * @since 0.2
 */
public final class CMonitorFeature implements CFeature {

  @Override
  public void init(CRuntime env) {
    env.register(new MonitorPool());
    CDetectResourceDeadlocks config = env.getConfiguration(CDetectResourceDeadlocks.class);
    env.register(new DeadlockDetector(config));
  }

  @Override
  public void shutdown(CRuntime env) {
    env.unregister(MonitorPool.class);
    env.unregister(DeadlockDetector.class);
  }

  @Override
  public Class<?> getInstrumentationAspect() {
    return YieldPoints.class;
  }

}
