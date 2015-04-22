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
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CCondition {
  
  /**
   * Type of condition.
   */
  enum Type {
    /**
     * Pre-condition.
     */
    PRE,
    /**
     * Post-condition.
     */
    POST
  }
  
  /**
   * Event.
   */
  enum Event {
    /**
     * Field update.
     */
    FIELD_UPDATE,
    
    /**
     * Method call.
     */
    METHOD_CALL;
  }
  
  /**
   * @return Identifier.
   */
  String id();
  
  /**
   * @return Condition type.
   */
  Type type();
  
  /**
   * @return Event type.
   */
  Event event();
  
  /**
   * @return Target class.
   */
  Class<?> targetClass();
  
  
  /**
   * @return A field name for {@link Event#FIELD_UPDATE} or a method signature for {@link Event#METHOD_CALL}.
   */
  String signature();
  
  /**
   * @return Condition to test.
   */
  String condition();
   
}
