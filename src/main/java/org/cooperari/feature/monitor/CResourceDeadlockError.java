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

import java.util.Iterator;
import java.util.List;

import org.cooperari.core.CThread;
import org.cooperari.core.scheduling.CThreadLocation;
import org.cooperari.errors.CDeadlockError;

/**
 * Exception thrown due to a deadlock error.
 *
 */
@SuppressWarnings("serial")
public class CResourceDeadlockError extends CDeadlockError {

  /**
   * Constructor.
   * 
   * @param t Thread where deadlock is detected.
   * @param cycle Monitor cycle due to the deadlock.
   */
  public CResourceDeadlockError(CThread t, List<Monitor> cycle) {
    super(formatMessage(t, cycle));
  }

  @SuppressWarnings("javadoc")
  private static String formatMessage(CThread t, List<Monitor> cycle) {
    StringBuilder sb = new StringBuilder();
    Iterator<Monitor> itr = cycle.iterator();
    Monitor m = itr.next();
    format(t, m, sb);
    while (itr.hasNext()) {
      sb.append(' ').append('>').append(' ');
      m = itr.next();
      format(m.getOwner(), m, sb);
    }
    return sb.toString();
  }

  @SuppressWarnings("javadoc")
  private static void format(CThread t, Monitor m, StringBuilder sb) {
    CThreadLocation pc = t.getLocation();
    sb.append('L').append(m.getId()).append('/').append(t.getName())
        .append('/').append(pc.getYieldPoint().getSourceFile()).append(':')
        .append(pc.getYieldPoint().getSourceLine());
  }

}
