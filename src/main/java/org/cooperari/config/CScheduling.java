package org.cooperari.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cooperari.scheduling.CScheduler;
import org.cooperari.scheduling.MemorylessScheduler;

/**
 * Configure thread scheduling.
 * 
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CScheduling {
  /**
   * @return Boolean value enabling the use of program state abstraction.
   */
  boolean useStateAbstraction() default true;
  
  /**
   * @return Type of scheduler to use.
   */
  Class<? extends CScheduler> scheduler() default MemorylessScheduler.class;
  
}