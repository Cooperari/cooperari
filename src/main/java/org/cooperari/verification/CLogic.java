package org.cooperari.verification;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.aspectj.weaver.ast.Not;

public class CLogic {

  @SuppressWarnings("unchecked")
  static <T extends Thread> boolean exists(Class<T> threadClass, Predicate<T> predicate) {
    return allThreads(threadClass).anyMatch(predicate);
  }
 
  @SuppressWarnings("unchecked")
  static <T extends Thread> boolean forall(Class<T> threadClass, Predicate<T> predicate) {
    return allThreads(threadClass).allMatch(predicate);
  }
  

  @SuppressWarnings("unchecked")
  static <T extends Thread> boolean count(Class<T> threadClass, Predicate<T> predicate, int count) {
    return allThreads(threadClass).filter(predicate).count() == count;
  }
 
  @SuppressWarnings("unchecked")
  private static <T extends Thread> Stream<T> allThreads(Class<T> threadClass) {
    return (Stream<T>) allThreads().stream().filter(t -> t.getClass() == threadClass);
  }
  private static <T extends Thread> Collection<Thread> allThreads() {
    return null;
  }
 
}
