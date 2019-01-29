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

package org.cooperari.feature.threadrunner;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cooperari.core.CThread;


/**
 * Yield points for calls to {@link org.cooperari.CSystem#forkAndJoin}.
 *
 * @since 0.2
 */
@Aspect
public class YieldPoints {
  /**
   * Around advice executed for a call to {@link org.cooperari.CSystem#forkAndJoin(Runnable...)}.
   * @param thisJoinPoint Join point.
   * @param runnables Runnable instances.
   * @throws IllegalThreadStateException In accordance to {@link org.cooperari.CSystem#forkAndJoin(Runnable...)}.
   * @throws Throwable Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(* org.cooperari.CSystem.forkAndJoin(Runnable...)) && args(runnables)")
  public void aroundCall(ProceedingJoinPoint thisJoinPoint, Runnable[] runnables) throws Throwable {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      ThreadRunnerOperation.execute(t, runnables);
    } else {
      thisJoinPoint.proceed(new Object[] { runnables} );
    }
  }
  
}
