package org.cooperari.errors;

/**
 * Base class for Cooperari exceptions.
 * 
 * @since 0.1
 *
 */
@SuppressWarnings("serial")
public class CError extends Error {

  /**
   * Constructs an error with no arguments.
   * @see Error#Error()
   */
  public CError() {

  }

  /**
   * Constructs error with an associated error message.
   * @param message Error message.
   * @see Error#Error(String)
   */
  public CError(String message) {
    super(message);
  }

  /**
   * Constructs error with an associated cause.
   * @param cause Cause for the error.
   * @see Error#Error(Throwable)
   */
  public CError(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs error with a message and a cause.
   * @param message Error message.
   * @param cause Cause for the error.
   * @see Error#Error(String,Throwable)
   */
  public CError(String message, Throwable cause) {
    super(message, cause);
  }
}
