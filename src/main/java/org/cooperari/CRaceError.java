package org.cooperari;

/**
 * Error thrown due to data races.
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
