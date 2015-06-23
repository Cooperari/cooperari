package org.cooperari.scheduling;

import java.util.List;

import org.cooperari.config.CScheduling;

/**
 * Program state factory.
 * 
 * @since 0.2
 */
public enum CSchedulerFactory {

  /**
   * Oblitus ("I forgot" in latin), a factory that creates memoryless schedulers .
   * 
   */
  OBLITUS {
    @Override
    public CScheduler create() {
      return new Oblitus();
    }
  },
  /**
   * Memini ("I remember" in latin), a factory that creates schedulers that 
   * remember past scheduling decisions for a given program state. 
   */
  MEMINI {
    @Override
    public CScheduler create() {
      return new Memini();
    }
  };
  /**
   * Create a new program state.
   * @return A new program state.
   */
  public abstract CScheduler create();

}
