package org.cooperari.feature.hotspots;

import org.cooperari.feature.CFeature;

/**
 * Hotspot reachibility feature.
 * 
 * @since 0.2
 *
 */
public final class CHotspotFeature implements CFeature {

  @Override
  public Class<?> getInstrumentationAspect() {
    return YieldPoints.class;
  }

  @Override
  public boolean cooperativeSemanticsRequired() {
    return false;
  }
}
