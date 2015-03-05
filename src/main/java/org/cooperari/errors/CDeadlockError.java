package org.cooperari.errors;


/**
 * Error thrown due to deadlock errors.
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
