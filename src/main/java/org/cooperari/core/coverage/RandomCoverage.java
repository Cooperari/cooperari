package org.cooperari.core.coverage;


import java.util.List;
import java.util.Random;

import org.cooperari.core.CRuntime;
import org.cooperari.core.CThread;
import org.cooperari.scheduling.CScheduler;


/**
 * Random coverage.
 *
 */
public class RandomCoverage implements CScheduler {


  
  /**
   * Pseudo-random number generator.
   */
  private Random _pRNG;
  /**
   * Constructor.
   * @param testClass Test class.
   * @param m Method.
   */
  public RandomCoverage(CRuntime runtime) {
    _pRNG = new Random(0); // a fixed seed is used for repeatable tests
  }
  @Override
  public CThread decision(List<CThread> readyThreads) {
    return readyThreads.get(_pRNG.nextInt(readyThreads.size()));
  }
  @Override
  public void onTestFinished() {
 
  }
  @Override
  public boolean done() {
    return false;
  }
  
  @Override
  public void onTestStarted() { }
  
  @Override
  public void onTestFailure() { }
}