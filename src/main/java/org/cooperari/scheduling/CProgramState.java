package org.cooperari.scheduling;

import java.util.List;
import java.util.Random;

/**
 * AbstRepresentation of program state.
 * 
 * <p>
 * A program state represents the state of the threads of the program in some form, comprising
 * both ready and blocked threads. Elements in the state must implements {@link CElement} and
 * may represent information for a single thread or groups of threads. 
 * </p>
 * <p>
 * <b>Note:</b>
 * Implementations of this interface must appropriately should define 
 * {@link Object#clone()}, {@link Object#equals(Object)}, and {@link Object#hashCode()}, 
 * in addition to the other abstract methods declared in the interface.
 * </p>
 * 
 * @since 0.2
 */
public interface CProgramState {


  
  /**
   * Program state element.
   * 
   * It may represents a group of threads or a single thread ({@link CThreadHandle}).
   *
   * @since 0.2
   */
  interface CElement { 
    /**
     * Get id.
     * @return An unique identifier for the element.
     */
    int getCID(); 

    /**
     * Get location.
     * @return A {@link CThreadLocation} object.
     */
    CThreadLocation getLocation();
  }

  /**
   * Get signature.
   * 
   * <p>
   * A signature is an unique and compact identification for a program state
   * that can be used by schedulers to do some form of book-keeping.
   * </p>
   * 
   * <p>
   * Signature objects should be immutable, have a low memory footprint,
   * and appropriately define {@link Object#equals(Object)} and {@link Object#hashCode()}.
   * </p>
   * @return The signature of the program state.
   */
  Object getSignature();
  
  /**
   * Get the number of state elements.
   *
   * @return Element count.
   */
  int size();

  /**
   * Get the number of threads represented by the program state.
   *
   * @return Thread count.
   */
  int threads();
  

  /**
   * Get all ready elements in the program state. 
   * @return A set view of the state's elements.
   */ 
  List<? extends CElement> readyElements();

  /**
   * Get all blocked elements in the program state. 
   * @return A set view of the state's elements.
   */ 
  List<? extends CElement> blockedElements();

  /**
   * Select a random ready thread from a program state.
   * The default implementation should not require overriding.
   * @param rng Random number generator to use.
   * @return A thread handle.
   */
  CThreadHandle select(Random rng);
 
  
  /**
   * Select a ready thread corresponding to a given element in the program state.
   * @param index Element index.
   * @param rng Random number generator to use (in case state elements are thread groups and 
   *        the method applies some kind of randomized logic to select the thread to returns).
   * @return A program state element.
   * @throws IndexOutOfBoundsException if index is not in the <code>[0, size()-1]</code> range.
   */
  CThreadHandle select(int index, Random rng) throws IndexOutOfBoundsException;
}
