package org.cooperari.feature.data;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.cooperari.core.CThread;
import org.cooperari.core.CWorkspace;

/**
 * AspectJ instrumentation for data access yield points.
 * 
 * @since 0.2
 */
@Aspect
public class YieldPoints {

  /**
   * Advice executed before field read accesses.
   * @param thisJoinPoint Join point.
   * @param o Target object.
   */
  @Before("get(* *.*) && target(o)")
  public void beforeGetField(JoinPoint thisJoinPoint, Object o) {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Read.before(t, o, thisJoinPoint.getSignature().getName());
    }
  }
  
  /**
   * Advice executed after field read accesses.
   * @param thisJoinPoint Join point.
   * @param o Target object.
   */
  @After("get(* *.*) && target(o)")
  public void afterGetField(JoinPoint thisJoinPoint, Object o) {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Read.after(t, o, thisJoinPoint.getSignature().getName());
    }
  }
  
  /**
   * Advice executed before field write accesses.
   * @param thisJoinPoint Join point.
   * @param o Target object.
   */
  @Before("set(* *.*) && target(o)")
  public void beforeSetField(JoinPoint thisJoinPoint, Object o) {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Write.before(t, o, thisJoinPoint.getSignature().getName());
    }
  }
  
  /**
   * Advice executed after field write accesses.
   * @param thisJoinPoint Join point.
   * @param o Target object.
   */
  @After("set(* *.*) && target(o)")
  public void afterSetField(JoinPoint thisJoinPoint, Object o) {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Write.after(t, o, thisJoinPoint.getSignature().getName());
    }
  }

  
  /**
   * Advice executed before {@code org.cooperari.CArray.cRead()}.
   * @param thisJoinPoint Join point.
   * @param array Array object.
   * @param index Index.
   * @see org.cooperari.CArray
   */
  @Before("call(* org.cooperari.CArray.cRead(*,int)) && args(array,index)")
  public void beforeArrayRead(JoinPoint thisJoinPoint, Object array, int index) {
    assert CWorkspace.debug(thisJoinPoint);
    
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Read.before(t, array, index);
    }
  }
  /**
   * Advice executed after {@code org.cooperari.CArray.cRead()}.
   * @param thisJoinPoint Join point.
   * @param array Array object.
   * @param index Index.
   * @see org.cooperari.CArray
   */
  @After("call(* org.cooperari.CArray.cRead(*,int)) && args(array,index)")
  public void afterArrayRead(JoinPoint thisJoinPoint, Object array, int index) {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Read.after(t, array, index);
    }
  }
  /**
   * Advice executed before {@code org.cooperari.CArray.cWrite()}.
   * @param thisJoinPoint Join point.
   * @param array Array object.
   * @param index Index.
   * @param value Value.
   * @see org.cooperari.CArray
   */
  @Before("call(* org.cooperari.CArray.cWrite(*,int,*)) && args(array,index,value)")
  public void beforeArrayWrite(JoinPoint thisJoinPoint, Object array, int index, Object value) {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Write.before(t, array, index);
    }
  }
  /**
   * Advice executed after {@code org.cooperari.CArray.cWrite()}.
   * @param thisJoinPoint Join point.
   * @param array Array object.
   * @param index Index.
   * @param value Value.
   * @see org.cooperari.CArray
   */
  @After("call(* org.cooperari.CArray.cWrite(*,int,*)) && args(array,index,value)")
  public void afterArrayWrite(JoinPoint thisJoinPoint, Object array, int index, Object value) {
    assert CWorkspace.debug(thisJoinPoint);
    CThread t = CThread.intercept(thisJoinPoint);
    if (t != null) {
      Write.after(t, array, index);
    }
  }
  
}
