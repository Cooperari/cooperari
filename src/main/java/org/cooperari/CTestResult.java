package org.cooperari;

import java.io.File;

/**
 * Execution result for a cooperative test.
 *  
 * @since 0.2
 */
public interface CTestResult {
  /**
   * Obtain failure status.
   * @return <code>true</code> if test failed.
   */
  boolean failed();
  
  /**
   * Get exception associated to failure, if test failed.
   * @return An exception object if there was a failure, <code>null</code> otherwise.
   */
  Throwable getFailure();
  
  /**
   * Get failure trace file. 
   * 
   * <p>The file will be defined, as long 
   * as the test failed and the failed test assertion is not due 
   * related to a {@link CSometimes} hotspot.</p>
   * 
   * @return A {@link java.io.File} instance identifying a failure trace, or <code>null</code>
   * if no failure trace was generated.
   */
   File getFailureTrace();
   
  /**
   * Get number of trials executed.
   * @return Number of executed test trials.
   */
  public int trials();
  
  /**
   * Get execution time.
   * @return Time taken for the execution of the test (all trials).
   */
  public long getExecutionTime(); 

}
