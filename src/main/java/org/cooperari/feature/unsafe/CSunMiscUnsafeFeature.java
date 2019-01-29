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

package org.cooperari.feature.unsafe;

import java.util.List;

import org.cooperari.core.CustomYieldPoint;
import org.cooperari.feature.CFeature;

/**
 * Feature handler for yield points defined by calls to methods in the <code>sun.misc.Unsafe</code> class.
 * 
 * @since 0.2
 *
 */
public class CSunMiscUnsafeFeature implements CFeature {

  @SuppressWarnings("restriction")
  @Override
  public void getCustomYieldPoints(List<CustomYieldPoint> list) {
    list.add(new CustomYieldPoint(sun.misc.Unsafe.class));
  }
}

