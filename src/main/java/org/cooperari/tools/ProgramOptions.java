package org.cooperari.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for {@link ProgramOption}.
 *
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProgramOptions {
   /**
    * @return Program options.
    */
   ProgramOption[] value() default {};
}
