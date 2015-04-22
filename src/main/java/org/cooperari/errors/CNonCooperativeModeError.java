package org.cooperari.errors;


/**
 * Exception thrown when some functionality requires a cooperative execution
 * environment, and is used from outside one.
 * 
 * @since 0.2
 *
 */
@SuppressWarnings("serial")
public class CNonCooperativeModeError extends CError {
  /**
   * Constructs a new error.
   */
  public CNonCooperativeModeError() {
    super("Non-cooperative execution environment!");
  }
}
