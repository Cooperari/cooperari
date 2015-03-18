package org.cooperari.verification;

import java.util.function.Predicate;

public class CLogic {

  static <T extends Thread> boolean exists(Class<T> threadClass, Predicate<T> predicate) {
    return false;
  }
 
  static <T extends Thread> boolean thereAre(Class<T> threadClass, Predicate<T> predicate, int count) {
    return false;
  }

  static <T extends Thread> boolean forAll(Class<T> threadClass, Predicate<T> predicate) {
    return false;
  }
  
  static <T extends Thread> boolean verify(T thread, Predicate<T> predicate) {
    return predicate.test(thread);
  }

  public static void main(String[] args) {
    System.out.println(verify(Thread.currentThread(), a -> a.isAlive()));
  }
}
