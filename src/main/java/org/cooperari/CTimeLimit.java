package org.cooperari;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure a time limit for test trials.
 * 
 * <p>
 * Further execution of test trials is prevented if the time elapsed since the
 * beginning of the first trial exceeds the time limit.
 * 
 * This configuration works in conjunction with {@link CMaxTrials} and the test
 * coverage type (@{link CCoverageType}).
 * </p>
 * 
 * @see CMaxTrials
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface CTimeLimit {
  /**
   * @return Time limit in seconds. The setting is ignored if lower or equal
   *         than 0. 
   */
  int value() default 0;
}
