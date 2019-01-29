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

package org.cooperari.core.util;

import java.util.Arrays;


/**
 * Raw tuple data.
 * 
 * @since 0.2 
 *
 */
public final class CRawTuple {
  
  /**
   * Array of objects defining the signature.
   */
  private final Object[] _data;
  
  /**
   * Cached hash code (computed at construction time).
   */
  private final int _hash;
  
  /**
   * Constructs a new signature.
   * @param args Signature arguments.
   */
  public CRawTuple(Object ...args) {
    _data = args;
    _hash = Arrays.deepHashCode(args);
  }
  
  /**
   * Get tuple data.
   * @return The data associated to this tuple.
   */
  public Object[] data() {
    return _data;
  }
  
  /**
   * Get hash code.
   * @return Integer value in line with the contract of {@link Object#hashCode()}.
   */
  @Override
  public int hashCode() {
    return _hash;
  }

  /**
   * Test equality.
   * @return Boolean value in line with the contract of {@link Object#equals(Object)}.
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o == null || o.getClass() != CRawTuple.class) {
        return false;
    }
    CRawTuple other = (CRawTuple) o;
    return _hash == other._hash && Arrays.deepEquals(_data, other._data);
  }
  
  /**
   * Get string representation.
   * @return String obtained using {@link Arrays#deepToString(Object[])} over the tuple's data.
   */
  @Override
  public String toString() {
    return Arrays.deepToString(_data);
  }
 
}
