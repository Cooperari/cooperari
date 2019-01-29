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

package org.cooperari.feature.data;

import org.cooperari.config.CRaceDetection;
import org.cooperari.core.CRuntime;
import org.cooperari.feature.CFeature;


/**
 * Handler for data access yield points.
 *
 * @since 0.2
 */
public final class CDataAccessFeature implements CFeature {

  @Override
  public void init(CRuntime env) {
    CRaceDetection config = env.getConfiguration(CRaceDetection.class);
    if (config.value()) {
      env.register(new RaceDetector(config));
    } 
  }

  @Override
  public void shutdown(CRuntime env) {
    if (env.get(RaceDetector.class) != null) {
      env.unregister(RaceDetector.class);
    }
  }

  @Override
  public Class<?> getInstrumentationAspect() {
    return YieldPoints.class;
  }

}
