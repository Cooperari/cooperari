package org.cooperari.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure the generation of coverage reports per test.
 * 
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CGenerateCoverageReports {
  /**
   * @return Boolean value enabling the generation of coverage reports (true by default).
   */
  boolean value() default true;
  
}