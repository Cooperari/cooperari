package org.cooperari.feature;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Record of all implemented features.
 * 
 * @since 0.2
 */
public final class CAllFeatures {
  /**
   * Array of all feature handlers.
   */
  private static final List<CFeature> ALL_FEATURES = 
      Collections.unmodifiableList(
          Arrays.asList(
              new org.cooperari.feature.atomic.CAtomicObjectsFeature(),
              new org.cooperari.feature.data.CDataAccessFeature(),
              new org.cooperari.feature.monitor.CMonitorFeature(),
              new org.cooperari.feature.hotspots.CHotspotFeature(),
              new org.cooperari.feature.thread.CThreadFeature(),
              new org.cooperari.feature.threadrunner.CThreadRunnerFeature(),
              new org.cooperari.feature.unsafe.CSunMiscUnsafeFeature()
              )
          );

  /**
   * Get list of all feature handlers.
   * @return A list of feature handler objects.
   */
  public static List<CFeature> getFeatures() {
    return ALL_FEATURES;
  }

  @SuppressWarnings("javadoc")
  private CAllFeatures() {

  }
}
