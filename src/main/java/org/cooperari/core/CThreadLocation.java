package org.cooperari.core;

import org.aspectj.lang.JoinPoint;
import org.cooperari.core.aspectj.AgentFacade;

/**
 * Information for the yield point of a cooperative thread.
 * 
 * <p>
 * The information comprises the location that identifies the point of yield,
 * plus the stage of operation.
 * </p>
 * <p>
 * The object returned by {#link getLocation()} is usually an instance of
 * {@link org.aspectj.lang.JoinPoint.StaticPart}, except for special internal
 * logic locations related to a thread's life-cycle (e.g., initialization,
 * shutdown).
 * </p>
 * 
 * <p>
 * The value returned by {#link getStage()} is usually <code>0</code> as most
 * thread interference points map to just one yield point. For operations that
 * proceed in several yield points like {@link Thread#wait()}. the information
 * is used to distinguish the distinct yield points (usually in sequence)
 * </p>
 * 
 * 
 * @since 0.2
 */
public final class CThreadLocation {

  /**
   * Thread location.
   */
  private final Object _location;

  /**
   * Stage for the operation at current interference point.
   * 
   */
  private final int _stage;

  /**
   * Computed hash code at construction for efficiency reasons.
   */
  private final int _hash;

  /**
   * Constructs yield point using supplied thread location key.
   * 
   * @param location thread location key
   */
  public CThreadLocation(Object location) {
    this(location, 0);
  }

  /**
   * Constructs yield point using supplied thread location key and stage
   * identification.
   * 
   * @param location Thread location.
   * @param stage Stage for the yield point.
   */
  public CThreadLocation(Object location, int stage) {
    _location = location;
    _stage = stage;
    _hash = _location.hashCode() ^ _stage;
  }

  /**
   * Get yield point location.
   * 
   * @return The yield point location.
   */
  public Object getLocation() {
    return _location;
  }

  /**
   * Get yield point stage.
   * 
   * @return The yield point location.
   */
  public int getStage() {
    return _stage;
  }

  /**
   * Get yield point signature.
   * <p>
   * If {@link #getLocation()} refers to an instance of
   * {@link org.aspectj.lang.JoinPoint.StaticPart}, the signature will identify the
   * the AspectJ join point. Otherwise, the textual representation of
   * {@link #getLocation()} is returned (as defined by {@link Object#toString}) is returned.
   * </p>
   * 
   * @return A string object.
   */
  public String getSignature() {
    if (! (_location instanceof JoinPoint.StaticPart)) {
      return _location.toString();
    }
    JoinPoint.StaticPart jpsp =  (JoinPoint.StaticPart) _location;
    String kind = jpsp.getKind();
    if (kind == JoinPoint.SYNCHRONIZATION_LOCK) {
      return AgentFacade.MONITOR_ENTER_JOINPOINT;
    }
    if (kind == JoinPoint.SYNCHRONIZATION_UNLOCK) {
      return AgentFacade.MONITOR_EXIT_JOINPOINT;
    }
    String shortSig = jpsp.getSignature().toString();
    shortSig =  shortSig.substring(shortSig.indexOf(' ') + 1);
    shortSig = shortSig.replace("java.lang.", "").replace('$', '.');
    if (kind == JoinPoint.METHOD_CALL) {
      return shortSig;
    }
    return kind + '(' + shortSig + ')';
  }

  /**
   * Get name of source file for the yield point, if this information is
   * available. The information will be defined if {{@link #getLocation()}
   * refers to an instance of {org.aspectj.lang.JoinPoint.StaticPart}.
   * 
   * @return The source file name or <code>"&lt;system&gt;"</code> if the information is not
   *         available.
   */
  public String getSourceFile() {
    return (_location instanceof JoinPoint.StaticPart) ? ((JoinPoint.StaticPart) _location)
        .getSourceLocation().getFileName() : "<system>";
  }

  /**
   * Get line number in source file for the yield point, if this information is
   * available. The information will be defined if {{@link #getLocation()}
   * refers to an instance of {org.aspectj.lang.JoinPoint.StaticPart}.
   * 
   * @return The line number for the source file or <code>0</code> if the
   *         information is not available.
   */
  public int getSourceLine() {
    return (_location instanceof JoinPoint.StaticPart) ? ((JoinPoint.StaticPart) _location)
        .getSourceLocation().getLine() : 0;
  }

  /**
   * Test for equality against given object.
   * 
   * <p>
   * For two equivalent yield points, the location keys must refer to exactly
   * the same object and have an equal stage value.
   * </p>
   * 
   * @return <code>true</code> if and if only <code>o</code> is a
   *         <code>CYieldPoint</code> object with the same location key object
   *         and with the same stage.
   * 
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof CThreadLocation))
      return false;

    CThreadLocation other = (CThreadLocation) o;
    return _location == other._location && _stage == other._stage;
  }

  /**
   * Get hash code for yield point.
   * 
   * @return The hash code value.
   */
  @Override
  public int hashCode() {
    return _hash;
  }

  /**
   * Get textual representation (used for debugging).
   * 
   * @return A <code>String</code>.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    toString(sb);
    return sb.toString();
  }

  /**
   * Append textual representation onto a {@link StringBuilder} instance. (used
   * for debugging).
   * 
   * @param sb {@link StringBuilder} instance.
   */
  public void toString(StringBuilder sb) {
    if (_location instanceof JoinPoint.StaticPart) {
      JoinPoint.StaticPart jpsp = (JoinPoint.StaticPart) _location;
      sb.append(jpsp.getSignature()).append(':').append(_stage).append("@")
      .append(jpsp.getSourceLocation().getFileName()).append(':')
      .append(jpsp.getSourceLocation().getLine());
    } else {
      sb.append(_location).append(':').append(_stage);
    }
  }

}
