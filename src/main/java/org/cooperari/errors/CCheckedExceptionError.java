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


/**
 * Error class used to wrap a checked exception.
 * 
 * <p>The wrapped checked Java exception (i.e., a {@link java.lang.Throwable} object
 * that is NOT an instance of {@link java.lang.Error} or {@link java.lang.RuntimeException}),
 * can be retrieved using {@link #getCause()} for this exception type.
 * 
 * @since 0.2
 *
 */
@SuppressWarnings("serial")
public class CCheckedExceptionError extends CError {
  /**
   * Constructs the error.
   * @param cause Cause for exception. It should NOT be an instance
   * of {@link java.lang.Error} or {@link java.lang.RuntimeException}.
   */
  public CCheckedExceptionError(Throwable cause) {
    super(cause);
  }
}
