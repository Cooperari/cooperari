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

import java.util.Arrays;
import java.util.Collection;

import org.cooperari.config.CAlways;
import org.cooperari.config.CNever;
import org.cooperari.config.CSometimes;

/**
 * Hotspot reachability error.
 * 
 * 
 * An error of this kind is thrown in one of the following cases:
 * <ol>
 * 
 * <li>A {@link CAlways}-specified hotspot for a test case IS NOT reached by a
 * test trial (for no failures, EVERY trial for the same test case must reach
 * the hotspot);</li>
 * 
 * <li>A {@link CNever}-specified hotspot for a test case IS reached by a test
 * trial (for no failures, NO test trial for the same test case should reach the
 * hotspot);</li>
 * 
 * <li>A {@link CSometimes}-specified hotspot for a test case IS NOT reached by ALL test trials
 * least one test trial (for for no failures, AT LEAST ONE test trial for the same test case should reach the
 * hotspot).</li>
 * 
 * </ol>
 * 
 * 
 * @since 0.2
 */
@SuppressWarnings("serial")
public final class CHotspotError extends Error {

  /**
   * Constructs a new error for given hotspot reachability annotation and a
   * collection of related hotspots.
   * 
   * @param annotation Annotation class (one of {@link CAlways},
   *        {@link CSometimes}, or {@link CNever}).
   * @param hotspots Hotspot collection.
   */
  public CHotspotError(Class<?> annotation, Collection<String> hotspots) {
    super(formatMessage(annotation, hotspots));
  }

  /**
   * Constructs a new error for given reachability annotation and specific
   * hotspot.
   * 
   * @param annotation Annotation class (one of {@link CAlways},
   *        {@link CSometimes}, {@link CNever}).
   * @param hotspot Hotspot collection.
   */
  public CHotspotError(Class<?> annotation, String hotspot) {
    this(annotation, Arrays.asList(hotspot));
  }

  @SuppressWarnings("javadoc")
  private static String formatMessage(Class<?> clazz,
      Collection<String> hotspots) {
    StringBuilder sb = new StringBuilder();
    sb.append(clazz.getSimpleName()).append(' ').append('{').append(' ');
    for (String h : hotspots) {
      sb.append('\"').append(h).append('\"').append(' ');
    }
    return sb.append('}').toString();
  }
}
