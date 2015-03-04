package org.cooperari.sanity.feature.monitors;

import static org.cooperari.core.CRuntime.getRuntime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cooperari.CSystem;
import org.cooperari.feature.monitor.Monitor;
import org.cooperari.feature.monitor.MonitorPool;
import org.cooperari.junit.CJUnitRunner;
import org.cooperari.sanity.feature.Data;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class) 
public class MonitorLockingTest  {

  private static void assertLockCount(Object o, int expected) {
    Monitor m = getRuntime().get(MonitorPool.class).get(o);
    if (m == null) 
      assertEquals("lock count", expected, 0);
    else 
      assertEquals("lock count", expected, m.getOwnerLockCount());
  }




  @Test
  public final void test1() {
    Object o = new Object();
    assertFalse(Thread.holdsLock(o));
    synchronized(o) { assertTrue(Thread.holdsLock(o)); }
    assertFalse(Thread.holdsLock(o)); 
  }

  @Test
  public final void test2() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();
    assertFalse(Thread.holdsLock(o1));
    assertFalse(Thread.holdsLock(o2));
    assertFalse(Thread.holdsLock(o3));
    synchronized(o1) { 
      assertTrue(Thread.holdsLock(o1));
      assertFalse(Thread.holdsLock(o2));
      assertFalse(Thread.holdsLock(o3));
      synchronized(o2) { 
        assertTrue(Thread.holdsLock(o1));
        assertTrue(Thread.holdsLock(o2));
        assertFalse(Thread.holdsLock(o3));
        synchronized(o3) { 
          assertTrue(Thread.holdsLock(o1));
          assertTrue(Thread.holdsLock(o2));
          assertTrue(Thread.holdsLock(o3));
        }
        assertTrue(Thread.holdsLock(o1));
        assertTrue(Thread.holdsLock(o2));
        assertFalse(Thread.holdsLock(o3));
      }
      assertTrue(Thread.holdsLock(o1));
      assertFalse(Thread.holdsLock(o2));
      assertFalse(Thread.holdsLock(o3));
    }
    assertFalse(Thread.holdsLock(o1));
    assertFalse(Thread.holdsLock(o2));
    assertFalse(Thread.holdsLock(o3));
  }

  @Test
  public void testNestedLock() {
    Object o = new Object();
    assertFalse(Thread.holdsLock(o));
    assertLockCount(o, 0);
    synchronized(o) { 
      assertTrue(Thread.holdsLock(o));
      assertLockCount(o, 1);
      synchronized(o) {  
        assertTrue(Thread.holdsLock(o));
        assertLockCount(o, 2);
        synchronized(o) { 
          assertTrue(Thread.holdsLock(o));
          assertLockCount(o, 3);
        }
        assertTrue(Thread.holdsLock(o));
        assertLockCount(o, 2);
      }
      assertTrue(Thread.holdsLock(o));
      assertLockCount(o, 1);
    }
    assertFalse(Thread.holdsLock(o));
    assertLockCount(o, 0);
  }

  private static Data SD1 = new Data();
  private static final Data SD2 = new Data();
  
  private static final Runnable 
  SHARE_DATA_RUNNABLE =  
  new Runnable() {
    @Override
    public void run() {
      assertFalse(Thread.holdsLock(SD1));
      assertFalse(Thread.holdsLock(SD2));
      synchronized (SD1) {
        assertTrue(Thread.holdsLock(SD1));
        assertFalse(Thread.holdsLock(SD2));
        synchronized (SD2) {
          assertTrue(Thread.holdsLock(SD1));
          assertTrue(Thread.holdsLock(SD2));
        }
        assertTrue(Thread.holdsLock(SD1));
        assertFalse(Thread.holdsLock(SD2));
      }
      assertFalse(Thread.holdsLock(SD1));
      assertFalse(Thread.holdsLock(SD2));
    }
  };

  
  @Test
  public final void test3() {
    CSystem.cRun(SHARE_DATA_RUNNABLE, SHARE_DATA_RUNNABLE);
  }

  
  @Test
  public final void test4() {
    CSystem.cRun(SHARE_DATA_RUNNABLE, SHARE_DATA_RUNNABLE, SHARE_DATA_RUNNABLE, SHARE_DATA_RUNNABLE);
  }
}
