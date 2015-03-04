package org.cooperari.core;

/**
 * An enumeration of common operation outcomes used by several primitives.
 * 
 * @since 0.2
 *
 */
public enum CBlockingOperationEvent {
  /**
   * Timeout. 
   */
  TIMEOUT_EVENT,
  /**
   * Interruption.
   */
  INTERRUPTION_EVENT,
  /**
   * Spurious wakeup. 
   */
  SPURIOUS_WAKEUP_EVENT,
  /**
   * Broken.
   */
  O_BROKEN;
}
