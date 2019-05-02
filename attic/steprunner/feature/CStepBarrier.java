package org.cooperari.feature.steprunner;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.cooperari.errors.CCheckedExceptionError;
import org.cooperari.errors.CNonCooperativeModeError;

/**
 * Step barrier for preemptive mode.
 */
public class CStepBarrier implements IStepBarrier {
  
  /**
   * Number of threads.
   */
  public final int count;
  
  /**
   * Threads that joined.
   */
  public final int joined;
  
  /**
   * Constructor.
   * @param count Thread count.
   */
  CStepBarrier(int count) {
    this.count = count;
    this.joined = 0;
  }

  @Override
  public void join() {
    throw new CNonCooperativeModeError();
  }

}
