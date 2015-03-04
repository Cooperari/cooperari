package org.cooperari.sanity.feature.thread;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
public class ThreadSleepTest {
  private static final int NANOS_PER_SECOND = 1000000;
  @Test
  public final void testSleep1() {
    final long DELTA_MS = 10;
    long t = System.nanoTime();
    try {
      Thread.sleep(DELTA_MS);
    } catch (InterruptedException e) {
      fail("Unexpected interruption");
    }
    t = System.nanoTime() - t;
    long expected = DELTA_MS * NANOS_PER_SECOND;
    assertTrue(String.format("Too little time: %d > %d (%d)", expected, t, expected-t), t - expected >= 0);
  }

  @Test
  public final void testSleep2() {
    final long DELTA_MS = 10;
    final int DELTA_NANO = 100;
    long t = System.nanoTime();
    try {
      Thread.sleep(DELTA_MS, DELTA_NANO);
    } catch (InterruptedException e) {
      fail("Unexpected interruption");
    }
    t = System.nanoTime() - t;
    long expected = DELTA_MS * NANOS_PER_SECOND + DELTA_NANO;
    assertTrue(String.format("Too little time: %d > %d (%d)", expected, t, expected-t), t - expected >= 0);
  }

  @Test 
  public final void testSleep3() {
    Thread.currentThread().interrupt();
    assertTrue(Thread.currentThread().isInterrupted());
    try {
      Thread.sleep(1);
      fail("expected InterruptedException");
    } catch (InterruptedException e) {
      assertFalse(Thread.currentThread().isInterrupted());
    }
  }

  @Test 
  public final void testSleep4() {
    Thread t = new Thread() {
      public void run() { 
        try {
          Thread.sleep(10000);
          fail("Expected InterruptedException");
        } catch (InterruptedException e) {
          assertFalse(Thread.currentThread().isInterrupted());
        }
      }
    };
    t.start();
    t.interrupt();
  }
  
  @Test 
  public final void testSleep5() {
    try {
      Thread.sleep(-1L);
      fail("Expected IllegalArgumentException");
    } 
    catch (InterruptedException e) {
      fail("unexpected interrupt");
    }
    catch(IllegalArgumentException e) {
    
    }
  }

  @Test  
  public final void testSleep6() {
    try {
      Thread.sleep(-1L, 0);
      fail("Expected IllegalArgumentException");
    } 
    catch (InterruptedException e) {
      fail("unexpected interrupt");
    }
    catch(IllegalArgumentException e) {
    
    }
  }

  @Test  
  public final void testSleep7() {
    try {
      Thread.sleep(0L, -1);
      fail("Expected IllegalArgumentException");
    } 
    catch (InterruptedException e) {
      fail("unexpected interrupt");
    }
    catch(IllegalArgumentException e) {
    
    }
  }

  @Test 
  public final void testSleep8() {
    try {
      Thread.sleep(0L, NANOS_PER_SECOND);
      fail("Expected IllegalArgumentException");
    } 
    catch (InterruptedException e) {
      fail("unexpected interrupt");
    }
    catch(IllegalArgumentException e) {
    
    }
  }
}
