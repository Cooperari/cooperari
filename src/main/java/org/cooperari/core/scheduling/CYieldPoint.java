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

package org.cooperari.core.scheduling;

/**
 * Yield point for a thread.
 *
 * @since 0.2
 */
public interface CYieldPoint extends Comparable<CYieldPoint> {

  /**
   * Lock acquisition (<code>monitorenter</code>) signature.
   */
  String MONITOR_ENTER_SIGNATURE = "<monitorenter>";

  /**
   * Lock acquisition (<code>monitorexit</code>) signature.
   */
  String MONITOR_EXIT_SIGNATURE = "<monitorexit>";

  /**
   * Thread initialization signature.
   */
  String THREAD_INITIALIZATION_SIGNATURE = "<init>";

  /**
   * Thread startup signature.
   */
  String THREAD_STARTED_SIGNATURE = "<started>";

  /**
   * Thread termination signature.
   */
  String THREAD_TERMINATED_SIGNATURE = "<terminated>";

  /**
   * Get signature. This represents the point of execution that caused the
   * thread yield.
   * 
   * @return The signature for the yield point.
   */
  public String getSignature();

  /**
   * Get source file name.
   * 
   * @return The source file for the weave point.
   */
  String getSourceFile();

  /**
   * Get source code line.
   * 
   * @return The source code line for the weave point.
   */
  int getSourceLine();

  /**
   * Get textual representation. The returned string contains information regarding the
   * the source file, the source line, and the signature.
   * 
   * @return A string object.
   */
  String toString();
}
