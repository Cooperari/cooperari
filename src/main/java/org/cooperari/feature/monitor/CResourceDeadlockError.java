package org.cooperari.feature.monitor;

import java.util.Iterator;
import java.util.List;

import org.cooperari.core.CThread;
import org.cooperari.core.CYieldPoint;
import org.cooperari.errors.CDeadlockError;

/**
 * Exception thrown due to a deadlock error.
 *
 */
@SuppressWarnings("serial")
public class CResourceDeadlockError extends CDeadlockError {

  /**
   * Constructor.
   * 
   * @param t Thread where deadlock is detected.
   * @param cycle Monitor cycle due to the deadlock.
   */
  public CResourceDeadlockError(CThread t, List<Monitor> cycle) {
    super(formatMessage(t, cycle));
  }

  @SuppressWarnings("javadoc")
  private static String formatMessage(CThread t, List<Monitor> cycle) {
    StringBuilder sb = new StringBuilder();
    Iterator<Monitor> itr = cycle.iterator();
    Monitor m = itr.next();
    format(t, m, sb);
    while (itr.hasNext()) {
      sb.append(' ').append('>').append(' ');
      m = itr.next();
      format(m.getOwner(), m, sb);
    }
    return sb.toString();
  }

  @SuppressWarnings("javadoc")
  private static void format(CThread t, Monitor m, StringBuilder sb) {
    CYieldPoint pc = t.getYieldPoint();
    sb.append('L').append(m.getId()).append('/').append(t.getName())
        .append('/').append(pc.getSourceFile()).append(':')
        .append(pc.getSourceLine());
  }

}
