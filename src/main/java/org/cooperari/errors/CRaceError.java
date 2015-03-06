package org.cooperari.errors;


/**
 * Data race error.
 * 
 * <p>
 * This error is thrown if and only if (1) race detection is enabled, (2) a data race is detected, 
 * and (3) the {@link org.cooperari.config.CRaceDetection#throwErrors()} configuration flag is set.
 * </p>
 * 
 * @since 0.2
 */
@SuppressWarnings("serial")
public final class CRaceError extends CError {
  /**
   * Constructs a new race error.
   * @param message Error message.
   */
  public CRaceError(String message) {
    super(message);
  }
}
