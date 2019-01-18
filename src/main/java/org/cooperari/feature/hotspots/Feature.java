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
  @Override
  public Class<?> getInstrumentationAspect() {
    return YieldPoints.class;
  }
  

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean cooperativeSemanticsRequired() {
    return false;
  }
}
