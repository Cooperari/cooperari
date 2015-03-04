package org.cooperari;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for the desired type of test coverage.
 * 
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CCoverage {
  /**
   * @return Desired type of coverage. The default value is {@link CCoverageType#HDC}.
   */
  CCoverageType value() default CCoverageType.HDC;
}