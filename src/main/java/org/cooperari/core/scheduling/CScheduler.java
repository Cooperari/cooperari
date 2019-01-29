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

package org.cooperari.core.scheduling;
import org.cooperari.config.CMaxTrials;
import org.cooperari.config.CTimeLimit;

/**
 * Abstract class for scheduler implementations.
 * 
 * @since 0.2
 */
public abstract class CScheduler {
  
  /**
   * Decide which thread should run next.
   * @param state Program state.
   * @return A thread handle corresponding to the decision.
   */
  public abstract CThreadHandle decision(CProgramState state);
  
  /**
   * Query method to determine if further test trials are necessary.
   * 
   * <p>
   * The method is called after each 
   * Schedulers may use this to signal the state-space of interleavings
   * has been covered according to some criterion.
   * </p>
   * 
   * <p>
   * The base implementation returns <code>true</code> 
   * implying that the number of (failure-free) test trials will only be bounded
   * by the {@link CMaxTrials} and {@link CTimeLimit} configurations.
   * </p>
   * 
   * @return Should return <code>true</code> if another test trial should execute.
   */
  public boolean continueTrials() {
    return true;
  }
  
  /**
   * Callback method invoked when a test trial starts.
   * This can be used for any setup actions.
   * The base implementation does nothing.
   */
  public void onTestStarted() {
    
  }
  /**
   * Callback method invoked when a test fails.
   * The base implementation does nothing.
   * @param failure Failure description.
   */
  public void onTestFailure(Throwable failure) {
    
  }
  /**
   * Callback method invoked when a test trial ends.
   * This can be used for cleanup actions.
   * The base implementation does nothing.
   */
  public void onTestFinished() {
    
  }
}