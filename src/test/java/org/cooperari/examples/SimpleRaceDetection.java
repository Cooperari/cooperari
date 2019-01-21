
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
 * Simple race detection examples.
 * 
 * @since 0.2
 */
@RunWith(CJUnitRunner.class)
@CRaceDetection(value=true,throwErrors=false)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({"javadoc"})
public class SimpleRaceDetection {

  static class SharedData { 
    int value = 0;
  }

  SharedData data;

  @Before 
  public void setup() {
    data = new SharedData();
  }

  @Test 
  public void test1() {
    CSystem.forkAndJoin(() -> data.value++, () -> data.value++);
    assertEquals(2, data.value);
  }

  // Same as test1, but throws an error as soon as a race is detected.
  @Test @CRaceDetection(value=true,throwErrors=true)
  public void test2() {
    CSystem.forkAndJoin(() -> data.value++, () -> data.value++);
    assertEquals(2, data.value);
  }

  // Test case where access is synchronized except for one thread.
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

  // Variant of test3 where only the read is unsynchronized.
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
  
  // Variant of test3 where only the write is unsynchronized.
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
