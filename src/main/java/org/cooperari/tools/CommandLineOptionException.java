package org.cooperari.tools;

/**
 * Exception thrown due to invalid command line options.
 * 
 * @since 0.2
 */
@SuppressWarnings("serial")
public class CommandLineOptionException extends Exception {

  /**
   * Constructs exception with associated error message.
   * @param message Error message.
   */
  public CommandLineOptionException(String message) {
    super(message);
  }
}
