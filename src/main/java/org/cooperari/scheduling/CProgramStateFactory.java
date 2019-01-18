package org.cooperari.scheduling;

import java.util.List;

/**
 * Program state factory.
 * 
 * @since 0.2
 */
public enum CProgramStateFactory {

  /**
   * Raw program-state factory.
   * This creates program states associated to individual threads.
   */
  RAW {
    @Override
    public CProgramState create(List<? extends CThreadHandle> readyThreads,
        List<? extends CThreadHandle> blockedThreads) {
      return new CRawProgramState(readyThreads, blockedThreads);
    }
  },
  /**
   * "Thread-group" program-state factory.
   * This creates program states abstracting the state of threads in groups.
   */
  GROUP {
    @Override
    public CProgramState create(List<? extends CThreadHandle> readyThreads,
        List<? extends CThreadHandle> blockedThreads) {
      return new CGroupProgramState(readyThreads, blockedThreads);
    }
  };
  /**
   * Create a new program state.
   * @param readyThreads List of ready threads.
   * @param blockedThreads List of blocked threads.
   * @return A new program state.
   */
  public abstract CProgramState create(List<? extends CThreadHandle> readyThreads, List<? extends CThreadHandle> blockedThreads);

}
