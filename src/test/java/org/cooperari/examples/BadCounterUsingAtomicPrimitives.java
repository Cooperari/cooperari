
package org.cooperari.examples;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;

import org.cooperari.CSystem;
import org.cooperari.config.CRaceDetection;
import org.cooperari.junit.CJUnitRunner;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Test of buggy counter using atomic primitives.
 * 
 * @since 0.2
 */
@RunWith(CJUnitRunner.class)
@CRaceDetection(value=true,throwErrors=false)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({"javadoc"})
public class BadCounterUsingAtomicPrimitives {

  // Naive counter implementation
  static class BuggyCounter { 
    AtomicBoolean access = new AtomicBoolean(true);
    volatile int value = 0;

    void increment() {
      while (! access.compareAndSet(true, false)) { }
      value ++;
      access.set(true);
    }

    void decrement() {
      // Bug: atomicity violation.
      while (! access.get()) { }
      access.set(false);
      value --;
      access.set(true);
    }
  }

  BuggyCounter theCounter;

  @Before 
  public void setup() {
    theCounter = new BuggyCounter();
  }

  @Test 
  public void test1() {
    CSystem.forkAndJoin(
        // 8 threads
        () -> { theCounter.increment(); theCounter.decrement(); },
        () -> { theCounter.increment(); theCounter.decrement(); },
        () -> { theCounter.increment(); theCounter.decrement(); },
        () -> { theCounter.increment(); theCounter.decrement(); },
        () -> { theCounter.increment(); theCounter.decrement(); },
        () -> { theCounter.increment(); theCounter.decrement(); },
        () -> { theCounter.increment(); theCounter.decrement(); },
        () -> { theCounter.increment(); theCounter.decrement(); }
        );
    assertEquals(0, theCounter.value);
  }

 

}
