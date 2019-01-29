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
import org.cooperari.junit.CJUnitRunner;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Example involving  monitors & races combined.
 *
 */
@RunWith(CJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SemaphoreWithBug {

  /** 
   * A buggy semaphore. 
   **/
  static class Semaphore {
    /** Value. */
    int value;

    /**
     * Constructor.
     * @param initialValue Initial value.
     */
    Semaphore(int initialValue) {
      value = initialValue;
    }


    /**
     * "Up" the semaphore.
     */
    public void up() {
      synchronized(this) {
        value++;
        notifyAll();
      }
    }

    /**
     * "Down" the semaphore.
     */
    public void down() {
      synchronized(this) {
        while (value == 0) {
          try {
            this.wait();
          } catch (InterruptedException e) {
            // ignore
          }
        }
      }
      // BUG: unsynchronized access.
      value--;
    }
    /**
     * Get value. 
     * @return The value of the semaphore.
     */
    public final int getValue() {
      synchronized(this) {
        return value;
      }
    }
  }


  /**
   * Test case: two threads "up" on the semaphore, two threads "down on it".
   */
  @Test
  public void test() {
    Semaphore s = new Semaphore(0);
    CSystem.forkAndJoin(
        () -> { s.up(); },
        () -> { s.up(); },
        () -> { s.down(); },
        () -> { s.down(); }
        );
    assertEquals(0, s.getValue());
  }
}


