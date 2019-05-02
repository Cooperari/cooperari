package org.cooperari.feature.steprunner;

import java.util.Iterator;
import java.util.List;

/**
 * Step runner. 
 */
public class StepRunner implements Runnable {

  /**
   * Steps.
   */
  private final List<Runnable> steps; 
  
  /**
   * Step barrier.
   */
  private final StepBarrier barrier;
  
  /**
   * Step runner.
   * @param steps Thread steps.
   * @param barrier Step barrier.
   */
  private StepRunner(List<Runnable> steps, StepBarrier barrier) {
    this.steps = steps;
    this.barrier = barrier;
  }
  
  @Override
  public void run() {
    Iterator<Runnable> itr = steps.iterator();
    StepBarrier b = barrier;
    while (itr.hasNext()) {
      Runnable step = itr.next();
      itr.remove();
      step.run();
      b.advance();
    }
  }
}