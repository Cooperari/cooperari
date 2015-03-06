package org.cooperari.errors;


/**
 * Error class used to wrap a checked exception.
 * 
 * <p>The wrapped checked Java exception (i.e., a {@link java.lang.Throwable} object
 * that is NOT an instance of {@link java.lang.Error} or {@link java.lang.RuntimeException}),
 * can be retrieved using {@link #getCause()} for this exception type.
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
