package org.cooperari.junit;
import java.util.WeakHashMap;

import org.cooperari.CTestResult;
import org.junit.runner.Description;

/**
 * Test result pool that provides a map from alive {@link org.junit.runner.Description} objects
 * to {@link org.cooperari.CTestResult} objects.
 * <p>
 * This lets classes like {@link org.cooperari.junit.CJUnitRunListener}
 * </p>
 * 
 * <p>
 * Implementation notes: 
 * <ul>
 * <li>A single-element enumeration scheme enforces the singleton pattern.</p>
 * <li>A {@link java.util.WeakHashMap} is used internally so that garbage entries 
 * for old tests can be automatically reclaimed by the JVM.</li>
 * </ul>
 * </p>
 * @since 0.2
 *
 */
enum CTestResultPool {
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
