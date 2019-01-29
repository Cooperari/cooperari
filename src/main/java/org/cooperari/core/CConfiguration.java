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

package org.cooperari.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.cooperari.config.CBaseConfiguration;
import org.cooperari.errors.CConfigurationError;

/**
 * Configuration object defined by annotations.
 * 
 * 
 * @since 0.2
 *
 */
public final class CConfiguration {

  /**
   * List of annotated elements where to look for configurations.
   */
  private ArrayList<AnnotatedElement> _list;

  /**
   * Constructs a new configuration.
   * @param primarySource Primary source for configuration.
   */
  public CConfiguration(AnnotatedElement primarySource) {
    _list = new ArrayList<>();
    _list.add(primarySource);
    if (primarySource.getClass() == Method.class) {
      _list.add( ((Method) primarySource).getDeclaringClass());
    }
    _list.add(CBaseConfiguration.class);
  }
  
  /**
   * Get configuration given by an annotation of a certain type.
   * 
   * @param <C> Annotation type for configuration.
   * @param type Class object for the configuration.
   * @throws CConfigurationError if configuration is not found.
   * @return An annotation object of type {@code T} if the configuration is
   *         defined, or <code>null</code> otherwise.
   * 
   */
  public <C extends Annotation> C get(Class<C> type) {
    for (AnnotatedElement src : _list) {
      C ann =  src.getAnnotation(type);
      if (ann != null) {
        return ann;
      }
    }
    throw new CConfigurationError("Missing @" + type.getSimpleName() + " annotation [" + type.getCanonicalName() + "].");
  }
}
