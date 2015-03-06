package org.cooperari.errors;


/**
 * Error thrown due to an unexpected internal Cooperari error.
 * 
 * @since 0.2
 *
 */
@SuppressWarnings("serial")
public class CInternalError extends CError {

  /**
   * Constructs an error with no arguments.
   */
  public CInternalError() {

  }

  /**
   * Constructs error with an associated error message.
   * @param message Error message.
   */
  public CInternalError(String message) {
    super(message);
  }

  /**
   * Constructs error with an associated cause.
   * @param cause Cause for the error.
   */
  public CInternalError(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs error with a message and a cause.
   * @param message Error message.
   * @param cause Cause for the error.
   */
  public CInternalError(String message, Throwable cause) {
    super(message, cause);
  }
}
