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

package org.cooperari.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Representation of a program option
 * 
 * @since 0.2
 */
@Repeatable(ProgramOptions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProgramOption {

  /**
   * @return Option switch identifier 
   */
  String value();
  
  
  /**
   * Get description.
   * @return A description of the program option.
   */
  String description();

  /**
   * @return <code>true</code> if option requires an argument.
   */
  boolean requiresArgument() default false;

  /**
   * @return A short id for the required argument.
   */
  String argumentId() default "";

  /**
   * @return Default value for required argument.
   */
  String defaultValue() default "";

}
