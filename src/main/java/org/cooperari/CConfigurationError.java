package org.cooperari;

/**
 * Configuration error.
 *  
 * @since 0.2
 */
@SuppressWarnings("serial")
public class CConfigurationError extends CError {
  /**
   * Constructs a new configuration error.
   * @param message Error message.
   */
  public CConfigurationError(String message) {
    super(message);
  }
  /**
   * Constructs a new configuration error.
   * @param message Error message.
   * @param cause Original cause for error.
   */
  public CConfigurationError(String message, Throwable cause) {
    super(message, cause);
  }
}
