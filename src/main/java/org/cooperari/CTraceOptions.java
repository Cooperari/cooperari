package org.cooperari;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure options for execution trace files.
 * 
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CTraceOptions {
  
  /**
   * @return The limit for the size of the trace (unbounded trace if lower or equal than 0).
   */
  int limit() default 0;
  
}
