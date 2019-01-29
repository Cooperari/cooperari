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
 * Scheduler factory enumeration.
 * 
 * @since 0.2
 */
public enum CSchedulerFactory {

  /**
   * Oblitus ("I forgot" in latin), a factory that creates memoryless schedulers .
   * 
   */
  OBLITUS {
    @Override
    public CScheduler create() {
      return new Oblitus();
    }
  },
  /**
   * Memini ("I remember" in latin), a factory that creates schedulers that 
   * remember past scheduling decisions for a given program state. 
   */
  MEMINI {
    @Override
    public CScheduler create() {
      return new Memini();
    }
  };
  /**
   * Create a new program state.
   * @return A new program state.
   */
  public abstract CScheduler create();

}
