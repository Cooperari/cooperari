package org.cooperari.scheduling;

import java.util.List;
import java.util.Random;

import org.cooperari.scheduling.CProgramState.CElement;

/**
 * Thread group handle. It represents a group of threads
 * in the same location.
 *
 * @since 0.2
 */
public interface CThreadGroupHandle extends CElement {
  /**
   * Get number of threads in the group.
   * @return A positive value.
   */
  int size();
  /**
   * Get threads associated to the group.
   * @return A list of thread handles.
   */
  List<CThreadHandle> threads();
  
  /**
   * Select a random thread from the group.
   * The default implementation should not require overriding.
   * @param rng Random number generator to use.
   * @return A thread handle from the group
   */
  default CThreadHandle selectAny(Random rng) {
    return threads().get(rng.nextInt(size()));
  }
}