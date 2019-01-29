//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.cooperari.core.scheduling;


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
public final class CThreadLocation implements Comparable<CThreadLocation> {

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
    return hashCode() == other.hashCode() && _stage == other._stage && _yieldPoint.compareTo(other._yieldPoint) == 0;  
  }

  /**
   * Get hash code for yield point.
   * 
   * @return The hash code value.
   */
  @Override
  public int hashCode() {
    return _yieldPoint.hashCode() ^  _stage;
  }

  /**
   * Get textual representation (used for debugging).
   * 
   * @return A <code>String</code>.
   */
  @Override
  public String toString() {
    return new StringBuilder().append(_yieldPoint).append(':').append(_stage).toString();
  }

  /**
   * Compare with given thread location.
   * @return The integer comparison value.
   */
  @Override
  public int compareTo(CThreadLocation other) {
    int cmp = _yieldPoint.compareTo(other._yieldPoint);
    if (cmp == 0) {
      cmp = _stage - other._stage;
    }
    return cmp;
  }
}
