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

import org.cooperari.errors.CRaceError;

/**
 * Annotation to enable race detection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CRaceDetection {
  /**
   * @return Boolean value enabling race detection. If enabled (the default) 
   * races will be logged to execution trace reports.
   *
   */
  boolean value() default true;
  
  /**
   * @return Boolean value indicating if {@link CRaceError} should be thrown when a race is detected.
   * If disabled (the default) races are merely reported.
   */
  boolean throwErrors() default false;
}