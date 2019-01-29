//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

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
   * Get list of all features.
   * @return The list of all implemented features.
   */
  public static List<CFeature> getFeatures() {
    return ALL_FEATURES;
  }

  @SuppressWarnings("javadoc")
  private CAllFeatures() {

  }
}
