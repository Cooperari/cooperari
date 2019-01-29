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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Abstract aspect for generic yield points.
 * 
 * <p>
 * Concrete aspects may extend this abstract aspect for custom yield points.
 * [using the technique documented <a href="https://eclipse.org/aspectj/doc/next/devguide/ltw-configuration.html">here</a>].
 * </p>
 * 
 * @since 0.2 
 *
 */
@Aspect
public abstract class ACustomYieldPoint {

  /**
   * Abstract method to define the pointcut/yield point.
   */
  @Pointcut
  public abstract void yieldPoint();
  
  /**
   * Advice executed before a method call.
   * @param thisJoinPoint Join point.
   */
  @Before("yieldPoint()")
  public final void beforeMethodCall(JoinPoint thisJoinPoint) {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      t.cYield(new CustomYieldPointOperation());
    }
  } 
}
