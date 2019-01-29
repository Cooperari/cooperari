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

import java.lang.reflect.Method;

/**
 * Custom yield point definition.
 * 
 * @since 0.2
 */
public final class CustomYieldPoint {
  
  /**
   * Aspect name.
   */
  private final String _aspectName;
  
  /**
   * Pointcut expression.
   */
  private final String _pointcutExpression;

  /**
   * Construct a custom yield point for calls to all declared methods of a given class.
   * @param clazz Class instance.
   */
  public CustomYieldPoint(Class<?> clazz) {
    _aspectName = "org.cooperari.inline_aspects." 
        + clazz.getCanonicalName().replace('.', '_');
    _pointcutExpression = String.format("call(* %s.*(..))", clazz.getCanonicalName());
  }
  
  /**
   * Construct a custom yield point for a Java method call.
   * @param method Method instance.
   */
  public CustomYieldPoint(Method method) {
    _aspectName = "org.cooperari.inline_aspects." 
        + method.getDeclaringClass().getCanonicalName().replace('.', '_')
        + "_"
        + method.getName();
    _pointcutExpression = String.format("call(* %s.%s(*))", method.getDeclaringClass().getCanonicalName(), method.getName());
  }
  
  /**
   * Get aspect class name for this yield point.
   * @return Name of the class to use for the inline aspect.
   */
  public String getAspectName() {
    return _aspectName;
  }
  
  /**
   * Get pointcut expression for yield point.
   * @return A pointcut expression for the yield point.
   */
  public String getPointcutExpression() {
    return _pointcutExpression;
  }

}
