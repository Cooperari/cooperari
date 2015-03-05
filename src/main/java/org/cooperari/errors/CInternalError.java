package org.cooperari.errors;


/**
 * Internal Cooperari error, related to unexpected events that occur during execution.
 * 
 * @since 0.2
 *
 */
@SuppressWarnings("serial")
public class CInternalError extends CError {

  /**
   * Constructs an error with no arguments.
   * @see Error#Error()
   */
  public CInternalError() {

  }

  /**
   * Constructs error with an associated error message.
   * @see Error#Error(String)
   */
  public CInternalError(String message) {
    super(message);
  }

  /**
   * Constructs error with an associated cause.
   * @param cause Cause for the error.
   * @see Error#Error(Throwable)
   */
  public CInternalError(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs error with a message and a cause.
   * @param message Error message.
   * @param cause Cause for the error.
   * @see Error#Error(String,Throwable)
   */
  public CInternalError(String message, Throwable cause) {
    super(message, cause);
  }
}
