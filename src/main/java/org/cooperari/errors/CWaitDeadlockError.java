package org.cooperari.errors;

import java.util.Collection;

import org.cooperari.core.CThread;
import org.cooperari.core.scheduling.CThreadLocation;


/**
 * Error thrown when all threads in a cooperative session
 * are in waiting state.
 * 
 * @since 0.2
 *
 */
@SuppressWarnings("serial")
public class CWaitDeadlockError extends CDeadlockError {

  /**
   * Constructs an error.
   * @param threads Deadlock threads.
   */
  public CWaitDeadlockError(Collection<CThread> threads) {
    super(formatMessage(threads));
  }
  
  @SuppressWarnings("javadoc")
  private static String formatMessage(Collection<CThread> threads) {
    StringBuilder sb = new StringBuilder();
    sb.append("Alive threads waiting indefinitely : {");
    for (CThread t : threads) {
      if (! t.isTerminated()) {
        CThreadLocation pc = t.getLocation();
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
