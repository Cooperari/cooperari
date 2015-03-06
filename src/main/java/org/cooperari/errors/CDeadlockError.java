package org.cooperari.errors;


/**
 * Deadlock error.
 * 
 * <p>An error of this type is thrown when a deadlock detected during cooperative execution.</p>
 * 
 * @since 0.2
 */
@SuppressWarnings("serial")
public class CDeadlockError extends CError {
  /**
   * Constructs a new race error.
   * @param message Error message.
   */
  public CDeadlockError(String message) {
    super(message);
  }
}
