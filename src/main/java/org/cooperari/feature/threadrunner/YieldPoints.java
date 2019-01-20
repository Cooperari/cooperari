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
