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

package org.cooperari;

import java.io.File;

import org.cooperari.config.CSometimes;

/**
 * Execution result for a cooperative test.
 *  
 * @since 0.2
 */
public interface CTestResult extends CCoverage {
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
