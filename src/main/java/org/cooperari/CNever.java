package org.cooperari;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure hotspots that should never be reached by a trial in a test session.
 * 
 * @see CAlways
 * @see CSometimes
 * 
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CNever {
  /**
   * @return Hotspot identifiers.
   */
  public String[] value() default {};
}
