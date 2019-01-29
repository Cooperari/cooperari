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
 * Base class for Cooperari errors.
 * 
 * @since 0.1
 *
 */
@SuppressWarnings("serial")
public class CError extends Error {

  /**
   * Constructs an error with no arguments.
   * @see Error#Error()
   */
  public CError() {

  }

  /**
   * Constructs error with an associated error message.
   * @param message Error message.
   * @see Error#Error(String)
   */
  public CError(String message) {
    super(message);
  }

  /**
   * Constructs error with an associated cause.
   * @param cause Cause for the error.
   * @see Error#Error(Throwable)
   */
  public CError(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs error with a message and a cause.
   * @param message Error message.
   * @param cause Cause for the error.
   * @see Error#Error(String,Throwable)
   */
  public CError(String message, Throwable cause) {
    super(message, cause);
  }
}
