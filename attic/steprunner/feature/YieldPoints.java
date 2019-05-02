package org.cooperari.feature.steprunner;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cooperari.core.CThread;
import org.cooperari.feature.thread.Yield;

/**
 * {@link java.lang.Thread} yield points.
 * 
 * @since 0.2
 */
@Aspect
public class YieldPoints {
  
  /**
   * Around advice executed in place of {@link StepBarrier#advance()}.
   * @param thisJoinPoint Join point.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(void CStepBarrier.join()) && target(sb)")
  public void aroundJoin(ProceedingJoinPoint thisJoinPoint, StepBarrier sb)  {
   /* CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Advance.execute(sb);
    } else {
      thisJoinPoint.proceed();
    }*/
  }
  
}