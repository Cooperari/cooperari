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


package org.cooperari.examples;

import static org.junit.Assert.*;

import org.cooperari.CSystem;
import org.cooperari.config.CMaxTrials;
import org.cooperari.config.CTraceOptions;
import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Dining philosophers example.
 * 
 * @since 0.2
 */
@RunWith(CJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/**
 * Dining philosophers example.
 * 
 * @since 0.2
 */
public class DiningPhilosophers {

  /**
   *  The fork class (could be just Object instead).
   */
  static class Fork { }

  /**
   * Philosopher thread.
   */
  static class Philosopher extends Thread {
    /** Left fork. */
    final Fork leftFork;
    /** Right fork. */
    final Fork rightFork;
    /** Flag indicating if philosopher has eaten. */
    boolean hasEaten;

    /**
     * Constructor.
     * @param id Id for philosopher.
     * @param left Left fork.
     * @param right Right fork.
     */
    public Philosopher(int id, Fork left, Fork right) {
      super("Philosopher-" + id);
      leftFork = left;
      rightFork = right;
      hasEaten = false;
    }

    @Override
    public void run() {
      synchronized(leftFork) {
        synchronized(rightFork) {
          hasEaten = true;
        }
      }
    }

    /**
     * Create a number of philosophers in the classic table
     * fashion.
     * @param n Number of philosophers.
     * @return An array of inactive philosopher threads (must be started).
     */
    static Philosopher[] create(int n) {
      Fork[] forks = new Fork[n];
      for (int i = 0; i < n; i++) {
        forks[i] = new Fork();
      }
      
      Philosopher[] philosophers = new Philosopher[n];
      for (int i = 0; i < n; i++) {
        Fork left = forks[i];
        Fork right = forks[(i+1) % n];
        philosophers[i] = new Philosopher(i, left, right);
      }
      
      return philosophers;
    }
  }

  /**
   * Test with 4 philosophers, with explicit fork and joining 
   * of all philosopher threads.
   * 
   * The <code>@CTraceOptions(logEveryTrace=true)</code> 
   * annotation in this method will cause every execution trace to be logged, 
   * rather than just (by default) the trace for the first test failure.
   * 
   * @throws InterruptedException (will never happen)
   */ 
  @Test @CTraceOptions(logEveryTrace=true)
  public void test_4_Philosophers_V1() throws InterruptedException {
    // Start philosophers
    Philosopher[] thePhilosophers = Philosopher.create(4);
    for (Philosopher p : thePhilosophers) {
      p.start();
    }
    // Wait for them to terminate -- maybe they will, maybe they won't :)
    for (Philosopher p : thePhilosophers) {
      p.join();
    }
    // Assert that all of them have eaten.
    for (Philosopher p : thePhilosophers) {
      assertTrue(p.hasEaten);
    }
  }

  /**
   * Test with 4 philosophers, using Cooperari's fork-and-join
   * API call. 
   * 
   * This is equivalent to {@link #test_4_Philosophers_V1()}, but
   * much more concise.
   */   
  @Test 
  public void test_4_Philosophers_V2() {
    Philosopher[] thePhilosophers = Philosopher.create(4);

    CSystem.forkAndJoin(thePhilosophers);

    for (Philosopher p : thePhilosophers) {
      assertTrue(p.hasEaten);
    }
  }

  /**
   * Test with 8 philosophers, using Cooperari's fork-and-join
   * API call. 
   * 
   * The number of default maximum test trials, 20, is insufficient to expose
   * the deadlock, hence we increase it to 100 using the
   * <code>@CMaxTrials(100)</code> annotation for the test method.
   * 
   */ 
  @Test @CMaxTrials(100)
  public void test_8_Philosophers() {
    Philosopher[] thePhilosophers = Philosopher.create(8);

    CSystem.forkAndJoin(thePhilosophers);

    for (Philosopher p : thePhilosophers) {
      assertTrue(p.hasEaten);
    }
  }

}
