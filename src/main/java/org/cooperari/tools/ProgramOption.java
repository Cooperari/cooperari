package org.cooperari.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

import org.cooperari.errors.CInternalError;

/**
 * Representation of a program option
 * 
 * @since 0.2
 */
@Repeatable(ProgramOptions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProgramOption {

  /**
   * @return Option switch identifier 
   */
  String value();
  
  
  /**
   * Get description.
   * @return A description of the program option.
   */
  String description();

  /**
   * @return <code>true</code> if option requires an argument.
   */
  boolean requiresArgument() default false;

  /**
   * @return A short id for the required argument.
   */
  String argumentId() default "";

  /**
   * @return Default value for required argument.
   */
  String defaultValue() default "";

}
