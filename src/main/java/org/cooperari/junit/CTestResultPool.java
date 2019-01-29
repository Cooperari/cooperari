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
import java.util.WeakHashMap;

import org.cooperari.CTestResult;
import org.junit.runner.Description;

/**
 * Test result pool that provides a map from alive {@link org.junit.runner.Description} objects
 * to {@link org.cooperari.CTestResult} objects.
 * 
 * <p>
 * Implementation notes: 
 * <ul>
 * <li>A single-element enumeration scheme enforces the singleton pattern.</li>
 * <li>A {@link java.util.WeakHashMap} is used internally so that garbage entries 
 * for old tests can be automatically reclaimed by the JVM.</li>
 * </ul>
 * 
 * 
 * @see org.cooperari.tools.cjunit.Main
 * @see org.cooperari.tools.cjunit.CJUnitRunListener
 * @since 0.2
 *
 */
public enum CTestResultPool {
  /**
   * Singleton instance.
   */
  INSTANCE;
  
  /**
   * The pool map.
   */
  private final WeakHashMap<Description,CTestResult> _map = new WeakHashMap<>();
  
  /**
   * Get test result, given a JUnit test description.
   * @param description JUnit test description.
   * @return The {@link CTestResult} entry for the JUnit test description if available. The description
   * should refer to an executed test.
   */
  public CTestResult getTestResult(Description description) {
    return _map.get(description);
  }
  
  /**
   * Get test result, given a JUnit test description.
   * @param description JUnit test description.
   * @param result Cooperative test result.
   * should refer to an executed test.
   */
  public void setTestResult(Description description, CTestResult result) {
    _map.put(description, result);
  }
}
