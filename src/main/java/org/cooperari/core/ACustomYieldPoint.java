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
