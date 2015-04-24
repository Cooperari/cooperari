package org.cooperari.scheduling;

import java.util.List;
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
   * @param runtime Runtime handle.
   */
  public MemorylessScheduler(CRuntime runtime) {
    _rng = new Random(0); // a fixed seed is used for repeatable tests
  }
  
  /**
   * Pick a random thread to run next.
   * The returned thread is a random one from the ready list.
   * @see CScheduler#decision(List, List)
   */
  @Override
  public CThreadHandle decision(List<? extends CThreadHandle> ready, List<? extends CThreadHandle> blocked) {
    return ready.get(_rng.nextInt(ready.size()));
  }
 
}