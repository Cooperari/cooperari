package org.cooperari.scheduling;


import java.util.List;
import java.util.Random;

import org.cooperari.core.CRuntime;
import org.cooperari.core.CThread;
import org.cooperari.scheduling.CScheduler;


/**
 * Random coverage.
 *
 */
public class MemorylessScheduler extends CScheduler {
  
  /**
   * Pseudo-random number generator.
   */
  private Random _rng;
  
  /**
   * Constructor.
   * @param runtime Runtime handle.
   */
  public MemorylessScheduler(CRuntime runtime) {
    _rng = new Random(0); // a fixed seed is used for repeatable tests
  }
  

  public CThreadHandle decision(CProgramState ps) {
    return ps.selectAnyThread(_rng);
  }
 
}