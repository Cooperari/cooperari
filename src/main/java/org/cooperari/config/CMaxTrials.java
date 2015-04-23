package org.cooperari.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure the maximum number of trials per test method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CMaxTrials {
  /**
   * @return Maximum trials per test. It must be a positive value:
   * a runtime error will be thrown in the execution engine if the value is lower or equal to 0.
   */
  int value() default 20; 
} 
