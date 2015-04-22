package org.cooperari.feature.hotspots;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.cooperari.core.CThread;


/**
 * Yield points for calls to {@link org.cooperari.CSystem#cHotspot(String)}
 * and {@link org.cooperari.CSystem#cHotspot(String,boolean)}.
 *
 * @since 0.2
 */
@Aspect
public class YieldPoints {

  /**
   * Advice executed before a call to {@link org.cooperari.CSystem#cHotspot(String)}.
   * @param thisJoinPoint Join point.
   * @param id Hotspot id.
   */
  @Before("call(* org.cooperari.CSystem.cHotspot(String)) && args(id)")
  public void beforeHotspot(JoinPoint thisJoinPoint, String id) {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      t.cYield(new HotspotOperation(id));
    }
  }
  
  /**
   * Advice executed before a call to {@link org.cooperari.CSystem#cHotspot(String,boolean)}.
   * @param thisJoinPoint Join point.
   * @param id Hotspot id.
   * @param cond Condition value.
   */
  @Before("call(* org.cooperari.cSystem.cHotspot(String,boolean)) && args(id,cond)")
  public void beforeHotspot(JoinPoint thisJoinPoint, String id, boolean cond) {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      t.cYield(new HotspotOperation(id, cond));
    }
  }

}
