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

import java.util.Collection;

import org.cooperari.core.CThread;
import org.cooperari.core.scheduling.CThreadLocation;


/**
 * Error thrown when all threads in a cooperative session
 * are in waiting state.
 * 
 * @since 0.2
 *
 */
@SuppressWarnings("serial")
public class CWaitDeadlockError extends CDeadlockError {

  /**
   * Constructs an error.
   * @param threads Deadlock threads.
   */
  public CWaitDeadlockError(Collection<CThread> threads) {
    super(formatMessage(threads));
  }
  
  @SuppressWarnings("javadoc")
  private static String formatMessage(Collection<CThread> threads) {
    StringBuilder sb = new StringBuilder();
    sb.append("Alive threads waiting indefinitely : {");
    for (CThread t : threads) {
      if (! t.isTerminated()) {
        CThreadLocation pc = t.getLocation();
        sb.append(' ')
          .append(t.getName())
          .append('/')
          .append(pc.getYieldPoint().getSourceFile())
          .append(':')
          .append(pc.getYieldPoint().getSourceLine())
          .append(' ');
      }
    }
    sb.append('}');
    return sb.toString();
  }
}
