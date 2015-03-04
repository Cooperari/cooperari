package org.cooperari.feature.thread;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cooperari.CInternalError;
import org.cooperari.CSystem;
import org.cooperari.core.CThread;
import org.cooperari.core.CWorkspace;

/**
 * {@link java.lang.Thread} yield points.
 * 
 * @since 0.2
 */
@Aspect
public class YieldPoints {

  /**
   * Around advice executed in place of {@link Thread#currentThread()}}.
   * The call does not define a yield point, but virtualized if executed by a cooperative thread.
   * @param thisJoinPoint Join point.
   * @return The current thread or what "appears" to be current thread.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(Thread Thread.currentThread())")
  public Thread aroundThreadCurrentThread(ProceedingJoinPoint thisJoinPoint) throws Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      t.cYield(CThread.NOP);
      Thread vt = t.getVirtualizedThread();
      return (vt != null) ? vt : t;
    } else {
      return (Thread) thisJoinPoint.proceed();
    }
  }
  
  /**
   * Around advice executed in place of {@link Thread#getState()}}.
   * @param thisJoinPoint Join point.
   * @param thread Target thread object.
   * @return The state of the thread.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(Thread.State Thread.getState()) && target(thread)")
  public Thread.State aroundGetState(ProceedingJoinPoint thisJoinPoint, Thread thread) throws Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread thisThread = CThread.intercept(thisJoinPoint);
    if (thisThread != null) {
      thisThread.cYield(CThread.NOP);
      return  Feature.getCThread(thread).getVirtualState();
    } 
    return (Thread.State) thisJoinPoint.proceed(new Object[]{ thread });
  }
  
  /**
   * Around advice executed in place of {@link Thread#isAlive()}}.
   * @param thisJoinPoint Join point.
   * @param thread Target thread object.
   * @return The state of the thread.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(boolean Thread.isAlive()) && target(thread)")
  public boolean aroundIsAlive(ProceedingJoinPoint thisJoinPoint, Thread thread) throws Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread thisThread = CThread.intercept(thisJoinPoint);
    if (thisThread != null) {
      thisThread.cYield(CThread.NOP);
      Thread.State s =  Feature.getCThread(thread).getVirtualState();
      return s != Thread.State.NEW && s != Thread.State.TERMINATED;
    } 
    return (Boolean) thisJoinPoint.proceed(new Object[]{ thread });
  }

  /**
   * Around advice executed in place of {@link Thread#interrupt()}.
   * @param thisJoinPoint Join point.
   * @param thread Target thread object.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(void Thread.interrupt()) && target(thread)")
  public void aroundInterrupt(ProceedingJoinPoint thisJoinPoint, Thread thread) throws Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Interrupt.execute(t, thread);
    } else {
      thisJoinPoint.proceed(new Object[]{ thread });
    }
  }

  /**
   * Around advice executed in place of {@link Thread#interrupted()}.
   * @param thisJoinPoint Join point.
   * @return A {@code boolean} value in accordance to {@link Thread#interrupted()}.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(boolean Thread.interrupted())")
  public boolean aroundInterrupted(ProceedingJoinPoint thisJoinPoint) throws Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    return t != null ?
        Interrupted.execute(t)
        :
          (Boolean) thisJoinPoint.proceed();
  }

  /**
   * Around advice executed in place of {@link Thread#isInterrupted()}.
   * @param thisJoinPoint Join point.
   * @param thread Target thread object.
   * @return A {@code boolean} in accordance to {@link Thread#isInterrupted()}.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(boolean Thread.isInterrupted()) && target(thread)")
  public boolean aroundIsInterrupted(ProceedingJoinPoint thisJoinPoint, Thread thread) throws Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    return t != null ?
        IsInterrupted.execute(t, thread)
        :
          (Boolean) thisJoinPoint.proceed(new Object[]{ thread });
  }

  /**
   * Around advice executed in place of {@link Thread#start()}.
   * @param thisJoinPoint Join point.
   * @param thread Target thread object.
   * @throws IllegalThreadStateException In accordance to {@link Thread#start()}.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(void Thread.start()) && target(thread)")
  public void aroundStart(ProceedingJoinPoint thisJoinPoint, Thread thread) throws IllegalThreadStateException, Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread thisThread = CThread.intercept(thisJoinPoint);
    if (thisThread != null) {
      Start.execute(thisThread, thread);
    } else {
      thisJoinPoint.proceed(new Object[]{ thread });
    }
  }

  /**
   * Around advice executed in place of {@link Thread#join()}.
   * @param thisJoinPoint Join point.
   * @param thread Target thread object.
   * @throws InterruptedException In accordance to {@link Thread#join()}.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(void Thread.join()) && target(thread)")
  public void aroundJoin(ProceedingJoinPoint thisJoinPoint, Thread thread) throws InterruptedException, Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread thisThread = CThread.intercept(thisJoinPoint);
    if (thisThread != null) {
      Join.execute(thisThread, thread, 0L);
    } else {
      thisJoinPoint.proceed(new Object[]{ thread });
    }
  }

  /**
   * Around advice executed in place of {@link Thread#join(long)}.
   * @param thisJoinPoint Join point.
   * @param thread Target thread object.
   * @param millis Argument for {@link Thread#join(long)}.
   * @throws InterruptedException In accordance to {@link Thread#join(long)}.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(void Thread.join(long)) && target(thread) && args(millis)")
  public void aroundJoin(ProceedingJoinPoint thisJoinPoint, Thread thread, long millis) throws InterruptedException, Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Join.execute(t,  thread, millis < 0 ? -1L : millis * 1000000L);
    } else {
      thisJoinPoint.proceed(new Object[]{ thread, millis });
    }
  }

  /**
   * Around advice executed in place of {@link Thread#join(long, int)}.
   * @param thisJoinPoint Join point.
   * @param thread Target thread object.
   * @param millis First argument for {@link Thread#join(long,int)}.
   * @param nano Second argument for {@link Thread#join(long,int)}.
   * @throws InterruptedException In accordance to {@link Thread#join(long)}.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(void Thread.join(long)) && target(thread) && args(millis, nano)")
  public void aroundJoin(ProceedingJoinPoint thisJoinPoint, Thread thread, long millis, int nano) throws InterruptedException, Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Join.execute(t,  thread, millis < 0 || nano < 0 || nano > 999999L ? -1L : millis * 1000000L + nano);
    } else {
      thisJoinPoint.proceed(new Object[]{ thread, millis + nano });
    }
  }

  /**
   * Around advice executed in place of {@link Thread#sleep(long)}.
   * @param thisJoinPoint Join point.
   * @param millis Argument for {@link Thread#sleep(long)}.
   * @throws InterruptedException In accordance to {@link Thread#sleep(long)}.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(void Thread.sleep(long)) && args(millis)")
  public void aroundSleep(ProceedingJoinPoint thisJoinPoint, long millis) throws InterruptedException, Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Sleep.execute(t, millis < 0 ? -1L : millis * 1000000L);
    } else {
      thisJoinPoint.proceed(new Object[] { millis });
    }
  }
  /**
   * Around advice executed in place of {@link Thread#sleep(long,int)}.
   * @param thisJoinPoint Join point.
   * @param millis First argument for {@link Thread#sleep(long,int)}.
   * @param nano Second argument for {@link Thread#sleep(long,int)}.
   * @throws InterruptedException In accordance to {@link Thread#sleep(long,int)}.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(void Thread.sleep(long,int)) && args(millis,nano)")
  public void aroundSleep(ProceedingJoinPoint thisJoinPoint, long millis, int nano) throws InterruptedException, Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Sleep.execute(t, millis < 0 || nano < 0 || nano > 999999L ? -1L : millis * 1000000L + nano);
    } else {
      thisJoinPoint.proceed(new Object[] { millis, nano });
    }
  }

  /**
   * Around advice executed in place of {@link Thread#stop()}.
   * @param thisJoinPoint Join point.
   * @param thread Target thread object.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(void Thread.stop()) && target(thread)")
  public void aroundStop(ProceedingJoinPoint thisJoinPoint, Thread thread) throws Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Stop.execute(t,  thread);
    } else {
      thisJoinPoint.proceed(new Object[]{ thread });
    }
  }
 

  /**
   * Around advice executed in place of {@link Thread#yield()}.
   * @param thisJoinPoint Join point.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   */
  @Around("call(void Thread.yield())")
  public void aroundYield(ProceedingJoinPoint thisJoinPoint) throws Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Yield.execute(t);
    } else {
      thisJoinPoint.proceed();
    }
  }

  /**
   * Around advice executed in place of {@link CSystem#cSpuriousWakeup(Thread)}.
   * @param thisJoinPoint Join point.
   * @param thread Target thread object.
   * @throws Throwable In accordance to {@link ProceedingJoinPoint#proceed()}.
   * @throws CInternalError if current (calling) thread is not cooperative.
   */
  @Around("call(void org.cooperari.CSystem.cSpuriousWakeup(java.lang.Thread)) && args(thread)")
  public void around(ProceedingJoinPoint thisJoinPoint, Thread thread) throws Throwable {
    assert CWorkspace.debug(thisJoinPoint);
    CThread thisThread = CThread.intercept(thisJoinPoint);
    if (thisThread != null) {
      SpuriousWakeup.execute(thisThread, thread);
    } else {
      throw new CInternalError("Current thread is not a cooperative one.");
    }
  }
}
