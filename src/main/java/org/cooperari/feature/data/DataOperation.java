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

package org.cooperari.feature.data;

import org.cooperari.core.COperation;

/**
 * Base class for data access operations.
 * @author Eduardo Marques, DI/FCUL, 2014-15.
 *
 */
public abstract class DataOperation extends COperation<Void> {
  /**
   * Constructs the operation.
   * @param theObject Object being accessed.
   * @param dataKey Data key, a <code>String</code> object for field accesses or a <code>Integer</code> object for array accesses.
   */
  public DataOperation(Object theObject, Object dataKey) {
    super(theObject, dataKey);
  }

}
