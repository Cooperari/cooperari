//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

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
public @interface CCooperativeOnly {

}
