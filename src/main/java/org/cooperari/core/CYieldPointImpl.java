package org.cooperari.core;

import org.aspectj.lang.JoinPoint;
import org.cooperari.scheduling.CYieldPoint;

/**
 * Yield point implementation.
 * 
 * @since 0.2
 *
 */
public final class CYieldPointImpl implements CYieldPoint {
  /**
   * Internal location used for "system" yield points.
   */
  public static final String INTERNAL = "<system>";

  /**
   * Constant for thread initialization.
   */
  public static final CYieldPoint THREAD_INITIALIZATION = new CYieldPointImpl(CYieldPoint.THREAD_INITIALIZATION_SIGNATURE, INTERNAL, 0);
  
  /**
   * Constant for thread start yield point.
   */
  public static final CYieldPoint THREAD_STARTED_YIELD_POINT = new CYieldPointImpl(CYieldPoint.THREAD_STARTED_SIGNATURE, INTERNAL, 0);
 
  /**
   * Constant for thread stop yield point.
   */
  public static final CYieldPoint THREAD_TERMINATED_YIELD_POINT = new CYieldPointImpl(CYieldPoint.THREAD_TERMINATED_SIGNATURE, INTERNAL, 0);
  
  /**
   * Signature.
   */
  private final String _signature;
  /**
   * Source code file.
   */
  private final String _file;

  /**
   * Source code line.
   */
  private final int _line;

  /**
   * Hash computed at construction time.
   */
  private final int _hash;

  /**
   * Constructs a yield point from supplied signature, file, and line.
   * @param signature Signature.
   * @param file Source code file.
   * @param line Source code line.
   */
  public CYieldPointImpl(String signature, String file, int line) {
    _signature = signature;
    _file = file;
    _line = line;
    _hash = signature.hashCode() ^ file.hashCode() ^ line;
  }

  /**
   * Constructs a yield point from supplied AspectJ join point information
   * @param jpsp Join point information.
   */
  CYieldPointImpl(JoinPoint.StaticPart jpsp) {
    this(deriveSignature(jpsp), jpsp.getSourceLocation().getFileName(), jpsp.getSourceLocation().getLine());
  }
  
  @SuppressWarnings("javadoc")
  private static String deriveSignature(JoinPoint.StaticPart jpsp) {
    String kind = jpsp.getKind(); // note that String is internalized
    if (kind == JoinPoint.SYNCHRONIZATION_LOCK) {
      return MONITOR_ENTER_SIGNATURE;
    }
    if (kind == JoinPoint.SYNCHRONIZATION_UNLOCK) {
      return MONITOR_EXIT_SIGNATURE;
    }
    String s = jpsp.getSignature().toString();
    s =  s.substring(s.indexOf(' ') + 1);
    s = s.replace("java.lang.", "").replace('$', '.');
    if (kind == JoinPoint.METHOD_CALL) {
      return s;
    }
    return kind + '(' + s + ')';
  }
  /**
   * @{inheritDoc}
   */
  @Override
  public String getSignature() {
    return _signature;
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public String getSourceFile() {
    return _file;
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public int getSourceLine() {
    return _line;
  }

  /**
   * Get hash code.
   * @return Hash code value.
   */
  @Override
  public int hashCode() {
    return _hash;
  }

  /**
   * Test for equality.
   * @param o Object reference.
   * @return <code>true</code> if the parameter represents an equivalent yield point.
   */
  @Override
  public boolean equals(Object o) {
    return o == this || ((o instanceof CYieldPoint) && compareTo((CYieldPoint) o) == 0);
  }
  
  /**
   * @{inheritDoc}
   */
  @Override
  public int compareTo(CYieldPoint o) {
    CYieldPointImpl other = (CYieldPointImpl) o;
    int c = _file.compareTo(other._file);
    if (c == 0) {
      c = _line - other._line;
      if (c == 0) {
        c = _signature.compareTo(other._signature);
      }
    }
    return c;
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public String toString() {
    return new StringBuilder().append(getSourceFile()).append('|')
        .append(getSourceLine()).append('|').append(getSignature()).toString();
  }
}
