package org.cooperari.scheduling;
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