package org.cooperari;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure bytecode instrumentation.
 * 
 * <p>
 * Specific classes may be indicated by {@link #classes()} and whole packages
 * by {@link #packages()}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CInstrument {
  /**
   * @return Whether to instrument the class that is annotated (typically yes).
   */
  boolean self() default true;
  
  /**
   * @return Whether to instrument inner classes for the class that is annotated (typically yes).
   */
  boolean selfInnerClasses() default true;
 
  /**
   * @return Whether to instrument classes in the same package as the one that is annotated.
   */
  boolean selfWholePackage() default true;
  
  /**
   * @return Specific classes to instrument.
   */
  Class<?>[] classes() default {};

  /**
   * @return Packages to instrument. Every class found in each package
   *         specified in this manner will be instrumented.
   */
  String[] packages() default {};
  
  /**
   * @return Whether to dump bytecode during load-time weaving (used for debugging).
   */
  boolean ltwDump() default false;
  
  /**
   * @return Load-time weaving options (used for debugging).
   */
  String ltwOptions() default "";
}