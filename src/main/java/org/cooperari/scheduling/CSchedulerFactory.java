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
   * Memory-less scheduler factory.
   */
  OBLIVISCI {
    @Override
    public CScheduler create() {
      return new MemorylessScheduler();
    }
  },
  /**
   * An improvement over {@link #OBLIVISCI}, which tries to avoid repeat scheduling decisions.
   */
  RECORDOR {
    @Override
    public CScheduler create() {
      return new HDScheduler();
    }
  };
  /**
   * Create a new program state.
   * @param readyThreads List of ready threads.
   * @param blockedThreads List of blocked threads.
   * @return A new program state.
   */
  public abstract CScheduler create();

}
