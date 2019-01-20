package org.cooperari.scheduling;

import java.util.Random;

/**
 * Memoryless scheduler.
 * 
 * <p>
 * This type of scheduler simply makes a random choice of thread at
 * every scheduling step. It maintains no information whatsoever 
 * of past scheduling  decisions.
 * The scheduler's decisions are deterministic however.
 * The pseudo-random number generator that is employed internally 
 * is always initialized with a fixed seed at construction time.
 * </p>
 * 
 * @since 0.2
 */
final class Oblitus extends CScheduler {

  /**
   * Pseudo-random number generator.
   */
  private Random _rng;

  /**
   * Constructor.
   */
  public Oblitus() {
    // A fixed seed (0) is used for repeatable tests.
    _rng = new Random(0); 
  }


  @Override
  public CThreadHandle decision(CProgramState state) {
    // Pick a random thread to run next.
    return state.select(_rng);
  }

}