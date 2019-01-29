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

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.cooperari.core.CWorkspace;
import org.cooperari.core.util.CRawTuple;


/**
 * History-dependent scheduler.
 * 
 * <p>
 * This scheduler is an improvement over the memoryless scheduler ({@link Oblitus}).
 * It tries to avoid repeated scheduling decisions whenever possible. 
 * It starts by trying a random choice and checks if it corresponds
 * to a previous decision for the same program state. 
 * If the choice is a repeated decision, the scheduler
 * then iterates the remaining program's state elements, 
 * looking for a decision that has not been made.
 * </p>
 * 
 * @since 0.2
 */
final class Memini extends CScheduler {

  /**
   * Pseudo-random number generator.
   */
  private Random _rng = new Random(0);

  /**
   * Log of previous decisions.
   */
  private final HashSet<CRawTuple> _log = new HashSet<>();

  /**
   * Count of a log size when a trial starts.
   */
  private int _prevLogSize = 0;

  /**
   * Constructor.
   */
  public Memini() {

  }

  @Override
  public void onTestStarted() {
    _prevLogSize = _log.size();
  }

  @Override
  public void onTestFailure(Throwable failure) {
    assert CWorkspace.debug("history: %d -> %d", _prevLogSize, _log.size());
  }

  @Override
  public void onTestFinished() {
    assert CWorkspace.debug("history: %d -> %d", _prevLogSize, _log.size());
  }


  /**
   * Check if further trials are necessary.
   * Further trials will be required if new decisions were recorded for the last one.
   * @return <code>true</code> if another test trial is required.
   */
  @Override
  public boolean continueTrials() {
    assert CWorkspace.debug("history: %d -> %d", _prevLogSize, _log.size());
    return  _prevLogSize != _log.size();
  }

  /**
   * Select the next thread to run.
   * @param state Program state.
   */
  @Override
  public CThreadHandle decision(CProgramState state) {
    final Object sig = state.getSignature();
    final List<? extends CProgramState.CElement> possibleChoices = state.readyElements();
    final int n = possibleChoices.size();
    final int firstChoice = _rng.nextInt(n);
    int choice = firstChoice;
    int tries = 0;
    CRawTuple d;
    CThreadHandle t;

    do {
      t = state.select(choice, _rng);
      d = new CRawTuple(choice, sig);
      choice = (choice + 1) % n;
      tries++;
      assert CWorkspace.debug(_log.contains(d) + " " + d.toString());
    } while (!_log.add(d) && tries < possibleChoices.size());
    assert CWorkspace.debug("D"+d.toString());

    return t;
  }
}