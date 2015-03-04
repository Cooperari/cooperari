package org.cooperari.sanity.feature.thread;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class) 
public class ThreadInterruptionTest {


  @Test
  public final void testNoInterrupt() {
    Thread t = Thread.currentThread();
    assertFalse("not interrupted", t.isInterrupted());
    assertFalse("test-and-clear clean interrupt status", Thread.interrupted());
  }
  
  @Test
  public final void testInterruptOnSelf() {
    Thread.currentThread().interrupt();
    assertInterruptionStatusCleanup();
  }
  
  @Test
  public final void testInterruptFromOtherThread() {
    Thread otherThread = new Thread() {
      public void run() {
        Thread t = Thread.currentThread();
        while(! t.isInterrupted()) { }
        assertInterruptionStatusCleanup();
      }
    };
    otherThread.start();
    otherThread.interrupt();
  }
  
  private void assertInterruptionStatusCleanup() {
    Thread t = Thread.currentThread();
    boolean status1 = t.isInterrupted();
    boolean status2 = Thread.interrupted();
    boolean status3 = t.isInterrupted();
    boolean status4 = Thread.interrupted();
    assertTrue("check interrupt status", status1);
    assertTrue("test and clear interrupt status", status2);
    assertFalse("clean interrupt status", status3);
    assertFalse("test and clear clean interrupt status", status4);
  }

}
