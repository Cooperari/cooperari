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
      .append(" exceptions:\n");
    for (Throwable t : list) {
      sb.append(' ')
        .append(t.toString())
        .append('\n');
    }
    return sb.toString();
  }
  
}
