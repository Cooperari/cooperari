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

package org.cooperari.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure a time limit for test trials.
 * 
 * <p>
 * Further execution of test trials is prevented if the time elapsed since the
 * beginning of the first trial exceeds the time limit.
 * 
 * This configuration works in conjunction with {@link CMaxTrials} and the test
 * coverage type (@{link CCoverageType}).
 * </p>
 * 
 * @see CMaxTrials
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface CTimeLimit {
  /**
   * @return Time limit in seconds. The setting is ignored if lower or equal
   *         than 0. 
   */
  int value() default 0;
}
