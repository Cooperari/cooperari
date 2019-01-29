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

package org.cooperari.feature.monitor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.cooperari.core.CThread;


/**
 * AspectJ instrumentation for monitor operations.
 * 
 * @since 0.2
 */
@Aspect
public final class YieldPoints {

  /**
   * Advice executed before monitor lock acquisition (the <code>monitorenter</code> instruction).
   * @param thisJoinPoint Join point.
   * @param o Target object.
   */
  @Before("lock() && args(o)")
  public void beforeMonitorEnter(JoinPoint thisJoinPoint, Object o) {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Enter.execute(t, o);
    }
  }
  
  /**
   * Advice executed before monitor lock release (the <code>monitorexit</code> instruction).
   * @param thisJoinPoint Join point.
   * @param o Target object.
   */
  @Before("unlock() && args(o)")
  public void beforeMonitorExit(JoinPoint thisJoinPoint, Object o) {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Exit.execute(t, o);
    }
  }
  
  /**
   * Around advice executed in place of <code>Thread.holdsLock()</code>.
   * @param thisJoinPoint Join point.
   * @param o Target object.
   * @return <code>true</code> if and only calling thread holds lock over <code>o</code>.
   * @throws Throwable In accordance to <code>ProceedingJoinPoint.proceed()</code>.
   */
  @Around("call(boolean Thread.holdsLock(Object)) && args(o)")
  public boolean aroundThreadHoldsLock(ProceedingJoinPoint thisJoinPoint, Object o) throws Throwable {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
     return ThreadHoldsLock.execute(t, o);
    } else {
      return (Boolean) thisJoinPoint.proceed(new Object[]{ o });
    }
  }
  /**
   * Around advice executed in place of <code>Object.wait()</code>.
   * @param thisJoinPoint Join point.
   * @param o Target object.
   * @throws InterruptedException In accordance to <code>Object.wait()</code>.
   * @throws Throwable In accordance to <code>ProceedingJoinPoint.proceed()</code>.
   */
  @Around("call(void Object.wait()) && target(o)")
  public void aroundWait(ProceedingJoinPoint thisJoinPoint, Object o) throws InterruptedException, Throwable {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Wait.execute(t, o, 0L);
    } else {
      thisJoinPoint.proceed(new Object[]{ o });
    }
  }
  
  /**
   * Around advice executed in place of <code>Object.wait(long)</code>.
   * @param thisJoinPoint Join point.
   * @param o Target object.
   * @param millis Argument for <code>Object.wait(long)</code>.
   * @throws InterruptedException In accordance to <code>Object.wait(long)</code>.
   * @throws Throwable In accordance to <code>ProceedingJoinPoint.proceed()</code>.
   */
  @Around("call(void Object.wait(long)) && target(o) && args(millis)")
  public void aroundWait(ProceedingJoinPoint thisJoinPoint, Object o, long millis) throws InterruptedException, Throwable {
    
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Wait.execute(t, o, millis * 1000000L);
    } else {
      thisJoinPoint.proceed(new Object[]{ o, millis });
    }
  }
  
  /**
   * Around advice executed in place of <code>Object.wait(long, int)</code>.
   * @param thisJoinPoint Join point.
   * @param o Target object.
   * @param millis First argument for <code>Object.wait(long, int)</code>.
   * @param nano Second argument for <code>Object.wait(long, int)</code>.
   * @throws InterruptedException In accordance to <code>Object.wait(long,int)</code>.
   * @throws Throwable In accordance to <code>ProceedingJoinPoint.proceed()</code>.
   */
  @Around("call(void Object.wait(long,int)) && target(o) && args(millis,nano)")
  public void aroundWait(ProceedingJoinPoint thisJoinPoint, Object o, long millis, int nano) throws InterruptedException, Throwable {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Wait.execute(t, o, millis * 1000000L + nano);
    } else {
      thisJoinPoint.proceed(new Object[]{ o, millis, nano });
    }
  }
  
  /**
   * Around advice executed in place of <code>Object.notify()</code>.
   * @param thisJoinPoint Join point.
   * @param o Target object.
   * @throws Throwable In accordance to <code>ProceedingJoinPoint.proceed()</code>.
   */
  @Around("call(void Object.notify()) && target(o)")
  public void aroundNotify(ProceedingJoinPoint thisJoinPoint, Object o) throws Throwable { 
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
     Notify.execute(t, o);
    } else {
      thisJoinPoint.proceed(new Object[]{ o });
    }
  }
  
  /**
   * Around advice executed in place of <code>Object.notify()</code>.
   * @param thisJoinPoint Join point.
   * @param o Target object.
   * @throws Throwable In accordance to <code>ProceedingJoinPoint.proceed()</code>.
   */
  @Around("call(void Object.notifyAll()) && target(o)")
  public void aroundNotifyAll(ProceedingJoinPoint thisJoinPoint, Object o) throws Throwable {
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      NotifyAll.execute(t, o);
    } else {
      thisJoinPoint.proceed(new Object[]{ o });
    }
  }

}
