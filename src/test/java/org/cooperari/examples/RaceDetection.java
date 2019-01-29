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
import org.cooperari.config.CRaceDetection;
import org.cooperari.junit.CJUnitRunner;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Race detection example.
 * 
 * @since 0.2
 */
@RunWith(CJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RaceDetection {

  /**
   * Simple class for shared data objects.
   */
  static class SharedData { 
    /** Integer value (initially 0). **/
    int value = 0;
  }

  /** Shared data object. */
  SharedData data;

  /**
   * JUnit test fixture (executed before each test).
   * It creates the shared data object.
   */
  @Before 
  public void setup() {
    data = new SharedData();
  }

  /**
   * Test two threads that increment the value non-atomically and
   * without any synchronization.
   * 
   * Races will merely be logged (the test will fail due to them)
   * but not cause any runtime errors immediately.
   */
  @Test 
  public void test1() {
    CSystem.forkAndJoin(() -> data.value++, () -> data.value++);
    assertEquals(2, data.value);
  }

  /**
   * Same as {@link #test1()} but throwing an error as soon
   * as a race is detected.
   */
  @Test @CRaceDetection(throwErrors=true)
  public void test2() {
    CSystem.forkAndJoin(() -> data.value++, () -> data.value++);
    assertEquals(2, data.value);
  }

  /**
   * Test with 4 threads: 
   * 3 of them use proper synchronization to update the 
   * data, but the remaining one does not it.
   */
  @Test 
  public void test3() {
    CSystem.forkAndJoin(
        () -> { data.value++; }, // misbehaved thread
        () -> { synchronized(data){ data.value++; } },
        () -> { synchronized(data){ data.value++; } },
        () -> { synchronized(data){ data.value++; } }
        );
    assertEquals(4, data.value);
  }

  /**
   * Variant of {@link #test3()} where only the read 
   * is unsynchronized.
   */
  @Test 
  public void test4() {
    CSystem.forkAndJoin(
        () -> { 
          // misbehaved thread
          int v = data.value;  
          synchronized(data) { 
            data.value = v + 1; 
          }
        },
        () -> { synchronized(data){ data.value++; } },
        () -> { synchronized(data){ data.value++; } },
        () -> { synchronized(data){ data.value++; } }
        );
    assertEquals(4, data.value);
  }

  /**
   * Variant of {@link #test5()} where only the write 
   * is unsynchronized.
   */
  @Test 
  public void test5() {
    CSystem.forkAndJoin(
        () -> { 
          // misbehaved thread
          int v;
          synchronized(data) {
            v = data.value;  
          }
          data.value = v + 1; 
        },
        () -> { synchronized(data){ data.value++; } },
        () -> { synchronized(data){ data.value++; } },
        () -> { synchronized(data){ data.value++; } }
        );
    assertEquals(4, data.value);
  }

}
