package org.cooperari.feature.steprunner;

import static org.cooperari.core.CThreadState.CREADY;
import static org.cooperari.core.CThreadState.CWAITING;

import org.cooperari.core.CThread;
import org.cooperari.core.CThreadState;
import org.cooperari.feature.thread.ThreadOperation;

/**
 * Step advance operation.
 *
 */
public class Advance extends ThreadOperation<Void> {

  /** 
   * Step info.
   */
  private final StepInfo stepInfo;

  /**
   * Advance operation.
   * @param si Step info. 
   */
  public Advance(StepInfo si) {
    stepInfo = si;
    stepInfo.threadCount --;
  }

  /**
   * Get state. 
   * @return {@link CThreadState#CREADY} if sync. counter reached 0, {@link CThreadState#CWAITING} otherwise.
   */
  @Override
  public CThreadState getState() {
    return stepInfo.threadCount == 0 ? CREADY : CWAITING;
  }

  /**
   * Complete operation.
   */
  @Override
  public void execute() {
    if (stepInfo.threadCount == 0) {
      stepInfo.threadCount = stepInfo.initialCount;
    }
  }
  
  /**
   * Execute operation.
   * @param thisThread This  thread.
   * @param si Step info.
   */
  public static void execute(CThread thisThread, StepInfo si) {
    thisThread.cYield(new Advance(si)); 
  }
}
  
