package org.cooperari.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cooperari.scheduling.CProgramStateFactory;
import org.cooperari.scheduling.CSchedulerFactory;

/**
 * Configure thread scheduling.
 * 
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CScheduling {
  /**
   * @return Program state factory to use.
   */
  CProgramStateFactory stateFactory() default CProgramStateFactory.GROUP;
  
  /**
   * @return Type of scheduler to use.
   */
  CSchedulerFactory schedulerFactory() default CSchedulerFactory.MEMINI;
  
}