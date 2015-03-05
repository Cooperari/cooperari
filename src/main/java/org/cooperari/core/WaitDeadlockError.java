package org.cooperari.core;

import java.util.Collection;

import org.cooperari.CDeadlockError;


/**
 * Error thrown when all threads in a cooperative session
 * are blocked.
 * 
 * @since 0.2
 *
 */
@SuppressWarnings("serial")
public class WaitDeadlockError extends CDeadlockError {

  /**
   * Constructs an error.
   * @param threads Deadlock threads.
   */
  WaitDeadlockError(Collection<CThread> threads) {
    super(formatMessage(threads));
  }
  
  @SuppressWarnings("javadoc")
  private static String formatMessage(Collection<CThread> threads) {
    StringBuilder sb = new StringBuilder();
    sb.append("Alive threads waiting indefinitely : {");
    for (CThread t : threads) {
      if (! t.isTerminated()) {
        CYieldPoint pc = t.getYieldPoint();
        sb.append(' ')
          .append(t.getName())
          .append('/')
          .append(pc.getSourceFile())
          .append(':')
          .append(pc.getSourceLine())
          .append(' ');
      }
    }
    sb.append('}');
    return sb.toString();
  }
}
