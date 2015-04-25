package org.cooperari.scheduling;

import org.aspectj.lang.JoinPoint;
import org.cooperari.core.CYieldPointImpl;

/**
 * Thread location information.
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
 * is used to distinguish the distinct yield points (usually in sequence) for
 * the same operation.
 * </p>
 * 
 * 
 * @since 0.2
 */
public final class CThreadLocation {

  /**
   * Yield point.
   */
  private final CYieldPoint _yieldPoint;

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
   * Constructs location related to a special system event.
   * @param id Event id.
   */
  public CThreadLocation(String id) {
    this(new CYieldPointImpl(id, "<system>", 0), 0);
  }
  
  /**
   * Constructs location based on yield point. 
   * The stage will be set to 0.
   * @param yp Yield point.
   */
  public CThreadLocation(CYieldPoint yp) {
    this(yp, 0);
  }
  
  /**
   * Constructs yield point using supplied thread location key and stage
   * identification.
   * 
   * @param yp Yield point.
   * @param stage Stage for the yield point.
   */
  public CThreadLocation(CYieldPoint yp, int stage) {
    _yieldPoint = yp;
    _stage = stage;
    _hash = _yieldPoint.hashCode() ^ _stage;
  }

  /**
   * Get yield point.
   * 
   * @return The yield point location.
   */
  public CYieldPoint getYieldPoint() {
    return _yieldPoint;
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
    return _hash == other._hash && _stage == other._stage && _yieldPoint.compareTo(other._yieldPoint) == 0;  
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
  private void toString(StringBuilder sb) {
    if (_yieldPoint instanceof JoinPoint.StaticPart) {
      JoinPoint.StaticPart jpsp = (JoinPoint.StaticPart) _yieldPoint;
      sb.append(jpsp.getSignature()).append(':').append(_stage).append("@")
      .append(jpsp.getSourceLocation().getFileName()).append(':')
      .append(jpsp.getSourceLocation().getLine());
    } else {
      sb.append(_yieldPoint).append(':').append(_stage);
    }
  }

}
