package org.cooperari;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure hotspots that should be reached at least by one trial in a test session.
 * 
 * @see CAlways
 * @see CNever
 * 
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CSometimes {
  /**
   * @return Hotspot identifiers.
   */
  public String[] value() default {};
}
