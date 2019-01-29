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

package org.cooperari.feature.thread;

import org.cooperari.core.COperation;

/**
 * Base class for operations involving thread methods.
 *
 * @param <T> Result type
 * @since 0.2
 */
public abstract class ThreadOperation<T> extends COperation<T> {

  /**
   * Constructs a thread operation with no arguments.
   * @see org.cooperari.core.COperation#COperation()
   */
  public ThreadOperation() {

  }

  /**
   * Constructs a thread operation with specified arguments.
   * @param arguments Operation arguments.
   * @see org.cooperari.core.COperation#COperation(Object[])
   */
  public ThreadOperation(Object... arguments) {
    super(arguments);
  }

}
