package org.cooperari.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to indicate that cooperative semantics
 * are required  JUnit test method to execute.
 * 
 * <p>
 * A running {@link CJUnitRunner} instance will bypass execution
 * for a test method marked with this annotation, if the execution
 * is in preemptive mode.
 * </p>
 *
 * <p><b>Example use</b></p>
 * <pre>
 * import org.junit.runner.RunWith;
 * import org.cooperari.CJUnitRunner;
 * ...
 * &#064;RunWith(CJUnitRunner.class)
 * public class MyTestClass {
 *   ...
 *   &#064;Test &#064;CRequire
 *   public void someTestMethod() { 
 *     // Standard JUnit runner will run this test only 
 *     // if cooperative semantics are enabled
 *     ...
 *   }
 * }
 * </pre>
 * 
 * @see CJUnitRunner
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CNonPreemptive {

}
