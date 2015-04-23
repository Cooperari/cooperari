package org.cooperari.core;

import java.util.Collection;

import org.cooperari.errors.CDeadlockError;
import org.cooperari.scheduling.CThreadLocation;


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
        CThreadLocation pc = t.location();
        sb.append(' ')
          .append(t.getName())
          .append('/')
          .append(pc.getYieldPoint().getSourceFile())
          .append(':')
          .append(pc.getYieldPoint().getSourceLine())
          .append(' ');
      }
    }
    sb.append('}');
    return sb.toString();
  }
}
