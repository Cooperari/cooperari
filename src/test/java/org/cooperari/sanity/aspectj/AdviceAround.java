package org.cooperari.sanity.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;

@SuppressWarnings("javadoc")
@Aspect
public class AdviceAround {
  @Around("call(* org.cooperari.sanity.aspectj.Counter.inc()) && target(c)")
  public void advice(JoinPoint thisJoinPoint, Counter c) {
    c.dec();
  }
}