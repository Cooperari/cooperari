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

import java.util.Random;

/**
 * Memoryless scheduler.
 * 
 * <p>
 * This type of scheduler simply makes a random choice of thread at
 * every scheduling step. It maintains no information whatsoever 
 * of past scheduling  decisions.
 * The scheduler's decisions are deterministic however.
 * The pseudo-random number generator that is employed internally 
 * is always initialized with a fixed seed at construction time.
 * </p>
 * 
 * @since 0.2
 */
final class Oblitus extends CScheduler {

  /**
   * Pseudo-random number generator.
   */
  private Random _rng;

  /**
   * Constructor.
   */
  public Oblitus() {
    // A fixed seed (0) is used for repeatable tests.
    _rng = new Random(0); 
  }


  @Override
  public CThreadHandle decision(CProgramState state) {
    // Pick a random thread to run next.
    return state.select(_rng);
  }

}