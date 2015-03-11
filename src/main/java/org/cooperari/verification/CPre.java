package org.cooperari.verification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CPre {
  
  /**
   * @return Property name.
   */
  String name();
  
  /**
   * 
   */
  Class<?> targetClass();
  
  /**
   * 
   */
  String method();
  
  /**
   * Condition.
   */
  String condition();
   
}
