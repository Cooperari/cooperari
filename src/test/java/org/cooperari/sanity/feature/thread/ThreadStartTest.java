package org.cooperari.sanity.feature.thread;

import static org.cooperari.CSystem.cHotspot;
import static org.junit.Assert.assertSame;

import org.cooperari.config.CAlways;
import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class) 
public class ThreadStartTest {

  private static class TestThread extends Thread {
    int value;
    
    TestThread(int v) {
      value = v;
    }
    
    @Override
    public void run() {
      assertSame(this, Thread.currentThread());
      if (value > 1) {
        new TestThread(value-1).start();
      }
      hotspot("run " + value);
    }
  }
  
  @Test @CAlways({"run 1"})
  public final void testOneThread() {
    new TestThread(1).start();
  }

  @Test @CAlways({"run 1", "run 2"})
  public final void testTwoThreads() {
    new TestThread(2).start();
  }
  
  @Test @CAlways({"run 1", "run 2", "run 3", "run 4", "run 5"})
  public final void testFiveThreads() {
    new TestThread(5).start();
  }
}
