package org.cooperari.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Record of all implemented features.
 * 
 * @since 0.2
 */
public final class Features {
  /**
   * Array of all feature handlers.
   */
  private static final List<FeatureHandler> ALL_FEATURES = 
      Collections.unmodifiableList(
          Arrays.asList(
              new org.cooperari.feature.atomic.Feature(),
              new org.cooperari.feature.data.Feature(),
              new org.cooperari.feature.monitor.Feature(),
              new org.cooperari.feature.hotspots.Feature(),
              new org.cooperari.feature.thread.Feature(),
              new org.cooperari.feature.threadrunner.Feature()
              )
          );

  /**
   * Get list of all feature handlers.
   * @return A list of feature handler objects.
   */
  public static List<FeatureHandler> getFeatureHandlers() {
    return ALL_FEATURES;
  }

  @SuppressWarnings("javadoc")
  private Features() {

  }
}
