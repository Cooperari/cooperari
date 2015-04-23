package org.cooperari.scheduling;

import java.util.List;

/**
 * Program state factory.
 * 
 * @since 0.2
 */
public final class CProgramStateFactory {

  /**
   * Factory method a new program state.
   * @param useAbstraction Use program state abstraction.
   * @param readyThreads List of ready threads.
   * @return A new program state.
   */
  public static CProgramState create(boolean useAbstraction, List<? extends CThreadHandle> readyThreads) {
    return null;
  }
  /**
   * Private constructor to avoid unintended instantation.
   */
  private CProgramStateFactory() { }
}
