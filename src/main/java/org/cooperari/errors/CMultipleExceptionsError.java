package org.cooperari.errors;

import java.util.Collections;
import java.util.List;

/**
 * Aggregation of exceptions thrown by multiple threads.
 * 
 * @since 0.2
 *
 */
@SuppressWarnings("serial")
public class CMultipleExceptionsError extends Error {
  
  /**
   * Exception list.
   */
  private final List<Throwable> _exceptions;
  
  /**
   * Constructs a new error from a collection of {@link Throwable} objects.
   * @param exceptions List of exceptions.
   */
  public CMultipleExceptionsError(List<Throwable> exceptions) {
    super(formatMessage(exceptions));
    _exceptions = exceptions;
  }

  /**
   * Get list of exceptions. The returned list is not modifiable.
   * @return List of exceptions. 
   */
  public List<Throwable> getExceptions() {
    return Collections.unmodifiableList(_exceptions);
  }
  
  @SuppressWarnings("javadoc")
  private static String formatMessage(List<Throwable> list) {
    StringBuilder sb = new StringBuilder();
    sb.append(list.size())
      .append(" exceptions:%n");
    for (Throwable t : list) {
      sb.append(' ')
        .append(t.toString())
        .append('%n');
    }
    return sb.toString();
  }
  
}
