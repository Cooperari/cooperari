package org.cooperari;

import java.util.List;

import org.cooperari.errors.CCheckedExceptionError;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Builder  for coarse-level schedule of multiple threads.
 * 
 */
public class CThreadScheduleBuilder {

  /**
   * Number of threads.
   */
  private final int numberOfThreads;
  
  /**
   * Steps per thread.
   */
  private final List<Runnable>[] stepsPerThread; 
  
  /**
   * Constructor.
   * @param numberOfThreads Number of threads.
   */
  @SuppressWarnings("unchecked")
  public CThreadScheduleBuilder(int numberOfThreads) {
    if (numberOfThreads <= 1) {
      throw new IllegalArgumentException("Expected thread count bigger than 1, not " + numberOfThreads); 
    }
    this.numberOfThreads = numberOfThreads;
    stepsPerThread = (List<Runnable>[]) new List[numberOfThreads];
    for (int i = 0; i < numberOfThreads; i++) {
      stepsPerThread[i] = new LinkedList<>();
    }
  }
  
  /**
   * Add a step.
   * @param runnables Runnable 
   */
  public void step(Runnable... runnables) {
    if (runnables == null || runnables.length != numberOfThreads) {
      throw new IllegalArgumentException("Expected array arguments of length: " + numberOfThreads); 
    }
    for (int i= 0; i < numberOfThreads; i++) {
      stepsPerThread[i].add(runnables[i]);
    }
  }
  
  /**
   * Build runnable objects for "step schedule".
   * @return An array of Runnable objects.
   */
  Runnable[] build() {
    Runnable[] runnables = new Runnable[numberOfThreads];
    StepBarrier barrier = new StepBarrier(numberOfThreads);
    for (int i = 0; i < numberOfThreads; i++) {
      runnables[i] = new StepRunner(stepsPerThread[i], barrier);
    }
    return runnables;
  }
  
  /**
   * Simple barrier.
   */
  static class StepBarrier {
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
  
  /**
   * Step runner. 
   */
  public static class StepRunner implements Runnable {

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
}
