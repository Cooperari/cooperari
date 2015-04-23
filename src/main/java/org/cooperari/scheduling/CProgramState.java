package org.cooperari.scheduling;

import java.util.List;
import java.util.Random;

/**
 * Representation of program state.
 * 
 * <p>
 * The program state represents the alive threads of the program, in
 * abstract or flat form. In abstract form, elements in the program 
 * stated are groups of threads at a common location 
 * (a "program counter" represented by {@link CThreadLocation}). 
 * In flat form, elements in the program state are individual threads.
 * </p>
 * 
 * @since 0.2
 */
public interface CProgramState {

  /**
   * Program state element.
   * 
   * It either represents a group of threads
   * in the same location ({@link CThreadGroupHandle} or a single thread ({@link CThreadHandle}), 
   * depending on whether program state abstraction is in use or not.
   *
   * @since 0.2
   */
  public interface CElement { 
    /**
     * Get id.
     * @return An unique identifier for the element.
     */
    int getCID(); 

    /**
     * Get location.
     * @return A {@link CThreadLocation} object.
     */
    CThreadLocation location();
  }

  /**
   * Indicate if program state abstraction is used.
   * @return <code>true</code> if elements are group of threads.
   */
  public boolean usesAbstraction();

  /**
   * Get the number of state elements.
   * This will be equal to {@link #threads()} if program state
   * abstraction is disabled. Otherwise it yields the number
   * of thread groups represented by the state.
   * @return The number of elements represented by the program state.
   */
  int size();

  /**
   * Get the number of threads represented by the state.
   * This will be equal to {{@link #size()} if program state
   * abstraction is disabled.
   * @return The number of threads represented by the program state.
   */
  int threads();

  /**
   * Get all elements in the program state. 
   * @return A set view of the state's elements.
   */
  List<? extends CElement> elements();

  /**
   * Select a random element from a program state.
   * The default implementation should not require overriding.
   * @param rng Random number generator to use.
   * @return A program state element.
   */
  default CElement selectAny(Random rng) {
    return elements().get(rng.nextInt(size()));
  }

  /**
   * Select a random thread from a program state.
   * The default implementation should not require overriding.
   * @param rng Random number generator to use.
   * @return A program state element.
   */
  default CThreadHandle selectAnyThread(Random rng) {
    CElement elem = selectAny(rng);
    return usesAbstraction() ? 
        ((CThreadGroupHandle) elem).selectAny(rng)
        : 
          (CThreadHandle) elem;
  }
}
