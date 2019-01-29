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

import java.util.HashSet;

import org.cooperari.config.CAlways;
import org.cooperari.config.CNever;
import org.cooperari.config.CSometimes;
import org.cooperari.core.CRuntime;
import org.cooperari.core.CWorkspace;
import org.cooperari.errors.CHotspotError;

/**
 * Hotspot handler.
 * 
 * @since 0.2
 *
 */
public final class HotspotHandler {

  /**
   * 'Sometimes' hotspots .
   */
  private final HashSet<String> _sHotspots = new HashSet<>();

  /**
   * 'Always' hotspots .
   */
  private final HashSet<String> _aHotspots = new HashSet<>();
  /**
   * 'Never' hotspots .
   */
  private final HashSet<String> _nHotspots = new HashSet<>();

  /**
   * 'Always' hotspots triggered during a test trial.
   */
  private final HashSet<String> _aHotspotsTrial = new HashSet<>();

  /**
   * Construct a new record considering reachability annotations for a given Java method.
   * @param runtime Method.
   */
  public HotspotHandler(CRuntime runtime) {
    configure(_aHotspots, runtime.getConfiguration(CAlways.class).value());
    configure(_nHotspots, runtime.getConfiguration(CNever.class).value());
    configure(_sHotspots, runtime.getConfiguration(CSometimes.class).value());

  }

  @SuppressWarnings("javadoc")
  private void configure(HashSet<String> set, String[] config) {
    for (String h : config) {
      set.add(h);
    }
  }
  
  /**
   * Callback method for when an hotspot is reached.
   * 
   * <p>
   * This method immediately throws {@link CHotspotError} in case the hotspot is a {@link CNever} hotspot.
   * Otherwise, it just marks the hotspot as reached.
   * </p>
   * @param id Hotspot id.
   * @throws CHotspotError if the hotspot is a {@link CNever} hotspot.
   */
  public void onHotspotReached(String id) {
    assert CWorkspace.debug("hotspot '%s' reached", id);
    if (_nHotspots.contains(id)) {
      assert CWorkspace.debug("@CNever hotspot '%s' has been reached!", id);
      throw new CHotspotError(CNever.class, id);
    }
    _aHotspotsTrial.remove(id);
    _sHotspots.remove(id);
  }

  /**
   * Method that should be called at the start of a test trial.
   */
  public void startTestTrial() {
    _aHotspotsTrial.clear();
    _aHotspotsTrial.addAll(_aHotspots);
  }

  /**
   * Method that should be called at the end of a test trial.
   * @throws CHotspotError if one or more {@link CAlways} hotspots have not been reached.
   * @see CAlways
   */
  public void endTestTrial() {
    if (!_aHotspotsTrial.isEmpty()) {
      for (String hotspot : _aHotspotsTrial) {
        CWorkspace.log("CAlways hotspot '%s' has not been reached!", hotspot);
      }
      throw new CHotspotError(CAlways.class, _aHotspotsTrial); 
    }
  }

  /**
   * Method that should be called at the end of a test session.
   * @throws CHotspotError if one or more {@link CSometimes} hotspots have not been reached.
   * @see CSometimes
   */
  public void endTestSession() {
    if (!_sHotspots.isEmpty()) {
      for (String hotspot : _sHotspots) {
        CWorkspace.log("CSometimes hotspot '%s' has never been reached!", hotspot);
      }
      throw new CHotspotError(CSometimes.class, _sHotspots); 
    }
  }
}
