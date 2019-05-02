package org.cooperari.feature.steprunner;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.cooperari.errors.CCheckedExceptionError;

/**
 * Step barrier for preemptive mode.
 */
public class PStepBarrier implements IStepBarrier {
  
  /**
   * Actual barrier.
   */
  CyclicBarrier barrier;
  
  /**
   * Constructor.
   * @param count Thread count.
   */
  PStepBarrier(int count) {
    this.barrier = new CyclicBarrier(count);
  }

  @Override
  public void join() {
    try {
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      throw new CCheckedExceptionError(e);
    }
  }

}
