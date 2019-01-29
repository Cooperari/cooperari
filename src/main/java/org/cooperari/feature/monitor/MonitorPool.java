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

package org.cooperari.feature.monitor;

import java.util.IdentityHashMap;

import org.cooperari.core.CWorkspace;

/**
 * Runtime pool for {@link Monitor} objects.
 * 
 * @since 0.2
 */
public final class MonitorPool {

  /**
   * Object-to-monitor map.
   */
  private final IdentityHashMap<Object, Monitor> _pool = new IdentityHashMap<>();
  
  /**
   * Monitor id counter.
   */
  private int _counter = 0;

  /**
   * Constructor.
   */
  public MonitorPool() {
    
  }
  
  /**
   * Equivalent to <code>get(o, false)</code>.
   * @param o Object.
   * @return A <code>CMonitor</code> object.
   */
  public Monitor get(Object o) {
    return get(o, false);
  }

  /**
   * Get monitor for an object.
   * @param o Object.
   * @param create Create monitor if undefined.
   * @return A <code>CMonitor</code> object.
   */
  public Monitor get(Object o, boolean create) {
    if (o == null) {
      // Null reference does not have an associated monitor.
      return Monitor.NULL;
    }
    Monitor m = _pool.get(o);
    if (create) {
      if (m == null) {
        m = new Monitor(_counter++, o);
        _pool.put(o, m);
        assert CWorkspace.debug("monitor %d created", m.getId());
      } else {
        m.addReference();
      }
    }
    if (m == null) {
      return Monitor.UNREFERENCED_MONITOR;
    }
    return m;
  }

  /**
   * Release monitor. 
   * 
   * This will dispose the monitor if the reference count of the monitor reaches <code>0</code>.
   * @param m Monitor.
   */
  public void release(Monitor m) {
    if (m.removeReference() == 0) {
      assert CWorkspace.debug("disposed of monitor " + m.getId());
      _pool.remove(m.getObject());
    }
  }
  
  /**
   * Get monitor count.
   * @return The number of monitors currently stored.
   */
  public int monitorCount() {
    return _counter;
  }

}
