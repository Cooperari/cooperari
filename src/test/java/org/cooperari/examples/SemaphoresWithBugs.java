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
public class SemaphoresWithBugs {

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
  public void test1() {
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


