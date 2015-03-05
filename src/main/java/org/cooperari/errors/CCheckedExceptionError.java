package org.cooperari.errors;


/**
 * Error class used to wrap a checked exceptions, that is an exceptions
 * that is NOT an instance of {@link java.lang.Error} or {@link java.lang.RuntimeException}.
 * The wrapped exception can be retrieved using {@link #getCause()}.
 * 
 * @since 0.2
 *
 */
@SuppressWarnings("serial")
public class CCheckedExceptionError extends CError {
  /**
   * Constructs the error.
   * @param cause Cause for exception. It should NOT be an instance
   * of {@link java.lang.Error} or {@link java.lang.RuntimeException}.
   */
  public CCheckedExceptionError(Throwable cause) {
    super(cause);
  }
}
