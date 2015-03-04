package org.cooperari.core;

import java.util.Collection;

import org.cooperari.CError;


@SuppressWarnings("serial")
public class WaitDeadlockError extends CError {

  public WaitDeadlockError() {
    
  }
  public WaitDeadlockError(Collection<CThread> threads) {
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
