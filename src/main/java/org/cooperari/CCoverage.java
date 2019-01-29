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

package org.cooperari;

/**
 * Interface for providers of basic test coverage information.
 *
 * @since 0.2
 */
public interface CCoverage {
 
  /**
   * Get number of yield points covered.
   * @return Number of yield points covered by test trials.
   */
  int getCoveredYieldPoints();

  /**
   * Get total yield points.
   * <p>
   * This figure indicates the total number of yield points that were not covered
   * for the same set of source files where at least one yield point was covered.
   * </p>
   * @return Number of yield points covered by all test trials.
   */
  int getTotalYieldPoints();

  /**
   * Get coverage rate.
   * 
   * <p>
   * The default method takes the {@link #getCoveredYieldPoints()} / {@link #getTotalYieldPoints()}
   * ratio. 
   * </p>
   * @return A value between <code>0</code> and <code>100</code>.
   */
  default double getCoverageRate() {
    return (getCoveredYieldPoints() * 100.0) / (double) getTotalYieldPoints();
  }
}
