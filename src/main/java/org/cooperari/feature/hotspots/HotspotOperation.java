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

package org.cooperari.feature.hotspots;

import org.cooperari.core.COperation;

/**
 * Operation for hotspot yield points.
 * @since 0.2
 *
 */
final class HotspotOperation extends COperation<Void> {
  /**
   * Constructs a new operation.
   * @param id Hotspot id.
   */
  HotspotOperation(String id) {
    super(id);
  }
  /**
   * Constructs a new  operation.
   * @param id Hotspot id.
   * @param cond Boolean condition value.
   */
  HotspotOperation(String id, boolean cond) {
    super(id, cond);
  }
}
