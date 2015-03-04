package org.cooperari.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to bypass cooperative execution for a JUnit test method.
 * 
 * <p>
 * A running {@link CJUnitRunner} instance will bypass cooperative execution
 * for a test method marked with this annotation.
 * The standard JUnit test runner and Java preemptive semantics are employed instead
 * for the execution of the test, and the test will only run once.
 * </p>
 *
 * <p><b>Example</b>
 * <pre>
 * import org.junit.runner.RunWith;
 * import org.cooperari.CJUnitRunner;
 * ...
 * &#064;RunWith(CJUnitRunner.class)
 * public class MyTestClass {
 *   ...
 *   &#064;Test &#064;CIgnore
 *   public void testMethod() { 
 *     // Standard JUnit runner will run this code preemptively and only once.
 *     ...
 *   }
 * }
 * </pre>
 * </p>
 * @see CJUnitRunner
 * @since 0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CIgnore {

}
