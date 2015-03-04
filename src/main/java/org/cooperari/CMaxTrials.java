package org.cooperari;

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
   * @return Maximum trials per test. The setting is ignored if the value is lower or equal to 0.
   */
  int value() default 1000; 
} 
