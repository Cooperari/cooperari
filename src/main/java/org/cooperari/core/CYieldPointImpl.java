package org.cooperari.core;

import org.aspectj.lang.JoinPoint;
import org.cooperari.CYieldPoint;
import org.cooperari.core.aspectj.AgentFacade;

/**
 * Yield point implementation.
 * @since 0.2
 *
 */
final class CYieldPointImpl implements CYieldPoint {

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
   * Precomputed hash.
   */
  private final int _hash;

  /**
   * Constructs a yield point from supplied signature, file, and line.
   * @param signature Signature.
   * @param file Source code file.
   * @param line Source code line.
   */
  CYieldPointImpl(String signature, String file, int line) {
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
   * Get hash code
   */
  @Override
  public int hashCode() {
    return _hash;
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

}
