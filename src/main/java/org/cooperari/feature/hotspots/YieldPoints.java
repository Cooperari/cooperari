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

package org.cooperari.feature.hotspots;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.cooperari.core.CThread;


/**
 * Yield points for calls to {@link org.cooperari.CSystem#hotspot(String)}
 * and {@link org.cooperari.CSystem#hotspot(String,boolean)}.
 *
 * @since 0.2
 */
@Aspect
public class YieldPoints {

  /**
   * Advice executed before a call to {@link org.cooperari.CSystem#hotspot(String)}.
   * @param thisJoinPoint Join point.
   * @param id Hotspot id.
   */
  @Before("call(* org.cooperari.CSystem.hotspot(String)) && args(id)")
  public void beforeHotspot(JoinPoint thisJoinPoint, String id) {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      t.cYield(new HotspotOperation(id));
    }
  }
  
  /**
   * Advice executed before a call to {@link org.cooperari.CSystem#hotspot(String,boolean)}.
   * @param thisJoinPoint Join point.
   * @param id Hotspot id.
   * @param cond Condition value.
   */
  @Before("call(* org.cooperari.cSystem.hotspot(String,boolean)) && args(id,cond)")
  public void beforeHotspot(JoinPoint thisJoinPoint, String id, boolean cond) {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      t.cYield(new HotspotOperation(id, cond));
    }
  }

}
