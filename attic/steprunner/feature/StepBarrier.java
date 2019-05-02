package org.cooperari.feature.steprunner;

import org.cooperari.errors.CCheckedExceptionError;

/**
 * Step barrier.
 */
public class StepBarrier {
  /**
   * Number of threads.
   */
  private final int numberOfThreads;
  
  /**
   * In counter for barrier. 
   */
  private int in;
  
  /**
   * Out counter for barrier.
   */
  private int out; 
  
  /**
   * Step barrier.
   * @param n Number of threads.
   */
  public StepBarrier(int n) {
    numberOfThreads = n;
    in = 0;
    out = 0;
  }
  
  /**
   * Advance to next step.
   */
  public synchronized void advance() {
    in ++;
    if (in == numberOfThreads) {
      notifyAll();
    } else {
      while (in != numberOfThreads) {
        try {
          wait();
        } catch (InterruptedException e) {
          throw new CCheckedExceptionError(e);
        }
      }
    }
    out++;
    if (out == numberOfThreads) {
      in = 0;
      out = 0;
      notifyAll();
    } else {
      while (out != 0) {
        try {
          wait();
        } catch (InterruptedException e) {
          throw new CCheckedExceptionError(e);
        }
      }
    } 
  }
}