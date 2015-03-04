package org.cooperari.feature.hotspots;

import org.cooperari.core.FeatureHandler;

/**
 * Hotspot reachibility feature.
 * 
 * @since 0.2
 *
 */
public final class Feature implements FeatureHandler {


  /**
   * {@inheritDoc}
   */
  public Class<?> getInstrumentationAspect() {
    return YieldPoints.class;
  }
}
