package org.cooperari.scheduling;

import java.util.Random;

import org.cooperari.core.CRuntime;


/**
 * Memoryless scheduler.
 * 
 * <p>
 * This type of scheduler simply makes a random choice of thread at
 * every scheduling step. It maintains no information whatsoever 
 * of past scheduling  decisions.
 * </p>
 * 
 * <p>
 * The scheduler's decisions are <b>deterministic</b> however.
 * The pseudo-random number generator that is employed internally 
 * is always initialized with a fixed seed at construction time.
 * </p>
 * 
 * @since 0.2
 */
public final class MemorylessScheduler extends CScheduler {
  
  /**
   * Pseudo-random number generator.
   */
  private Random _rng;
  
  /**
   * Constructor.
   */
  public MemorylessScheduler() {
    _rng = new Random(0); // a fixed seed is used for repeatable tests
  }
  
  /**
   * Pick a random thread to run next.
   */
  @Override
  public CThreadHandle decision(CProgramState state) {
    return state.select(_rng);
  }
 
}