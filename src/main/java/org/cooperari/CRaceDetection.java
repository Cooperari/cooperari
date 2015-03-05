package org.cooperari;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable race detection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CRaceDetection {
  /**
   * @return Boolean value enabling race detection. Races will be logged
   * to execution trace reports.
   *
   */
  boolean value() default false;
  
  /**
   * @return Boolean value indicating if {@link CRaceError} should be thrown when a race is detected.
   * If disabled (the default) races are merely .
   */
  boolean throwErrors() default false;
}