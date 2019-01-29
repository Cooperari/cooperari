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
 * Configure bytecode instrumentation.
 * 
 * <p>
 * Specific classes may be indicated by {@link #classes()} and whole packages
 * by {@link #packages()}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CInstrument {
  /**
   * @return Whether to instrument the class that is annotated (typically yes).
   */
  boolean self() default true;
  
  /**
   * @return Whether to instrument inner classes for the class that is annotated (typically yes).
   */
  boolean selfInnerClasses() default true;
 
  /**
   * @return Whether to instrument classes in the same package as the one that is annotated.
   */
  boolean selfWholePackage() default true;
  
  /**
   * @return Specific classes to instrument.
   */
  Class<?>[] classes() default {};

  /**
   * @return Packages to instrument. Every class found in each package
   *         specified in this manner will be instrumented.
   */
  String[] packages() default {};
  
  /**
   * @return Whether to dump bytecode during load-time weaving (used for debugging).
   */
  boolean ltwDump() default true;
  
  /**
   * @return Extra load-time weaving options (used for debugging).
   */
  String ltwOptions() default "";
}