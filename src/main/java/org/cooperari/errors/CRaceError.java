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

package org.cooperari.errors;


/**
 * Data race error.
 * 
 * <p>
 * This error is thrown if and only if (1) race detection is enabled, (2) a data race is detected, 
 * and (3) the {@link org.cooperari.config.CRaceDetection#throwErrors()} configuration flag is set.
 * </p>
 * 
 * @since 0.2
 */
@SuppressWarnings("serial")
public final class CRaceError extends CError {
  /**
   * Constructs a new race error.
   * @param message Error message.
   */
  public CRaceError(String message) {
    super(message);
  }
}
