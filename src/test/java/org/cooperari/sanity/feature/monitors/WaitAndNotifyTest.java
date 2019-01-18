package org.cooperari.sanity.feature.monitors;

import static org.cooperari.CSystem.hotspot;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.cooperari.CSystem;
import org.cooperari.config.CAlways;
import org.cooperari.config.CNever;
import org.cooperari.config.CSometimes;
import org.cooperari.core.CRuntime;
import org.cooperari.core.WaitDeadlockError;
import org.cooperari.junit.CJUnitRunner;
import org.cooperari.sanity.feature.Data;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
public class WaitAndNotifyTest  {

  private static final int NANOS_PER_SECOND = 1000000;

  private static Data SHARED_DATA = new Data();

  @Test
  public final void testUselessNotify() {
    Object o = new Object();
    synchronized (o) {
      o.notify();
    }
  }

  @Test
  public final void testUselessNotifyAll() {
    Object o = new Object();
    synchronized (o) {
      o.notifyAll();
    }  
  }

  @Test
  public final void testUselessWait1() {
    final int DELTA = 10;
    long t = System.nanoTime();
    Object o = new Object();
    synchronized (o) {
      try {
        o.wait(DELTA);
      } catch (InterruptedException e) {
        fail("unexpected interrupt");
      }
    }  
    t = System.nanoTime() - t;
    long expected = DELTA * NANOS_PER_SECOND;
    assertTrue(String.format("Too little time: %d > %d", expected, t), t - expected >= 0);
  }

  @Test
  public final void testUselessWait2() {
    final long DELTA_ms = 10;
    final int DELTA_nano = 10;
    long t = System.nanoTime();
    Data d = new Data();
    synchronized (d) {
      try {
        d.wait(DELTA_ms, DELTA_nano);
      } catch (InterruptedException e) {
        fail("Interruption not expected");
      }
    }
    t = System.nanoTime() - t;
    long expected = DELTA_ms * NANOS_PER_SECOND + DELTA_nano;
    assertTrue(String.format("Too little time: %d > %d", expected, t), t - expected >= 0); 
  }


  private static final Runnable WAIT_FOREVER = new Runnable() {
    public void run() {
      Object o = new Object();
      synchronized(o) {
        try { 
          o.wait(); 
          hotspot("DONE"); 
        } 
        catch (InterruptedException e) { 
          hotspot("INT"); 
          assertFalse("interrupt status not clear", Thread.currentThread().isInterrupted());   
       }
      }
    }
  };

  @Test(expected=WaitDeadlockError.class) @CNever({"INT","DONE"})
  public final void testBlockingWait() {
    CSystem.forkAndJoin(WAIT_FOREVER);
  }

  @Test(expected=WaitDeadlockError.class) @CNever({"INT","DONE"})
  public final void testBlockingWait2() {
    CSystem.forkAndJoin(WAIT_FOREVER, WAIT_FOREVER);
  }

  @Test(expected=WaitDeadlockError.class) @CNever({"INT","DONE"})
  public final void testBlockingWait4() {
    CSystem.forkAndJoin(WAIT_FOREVER, WAIT_FOREVER, WAIT_FOREVER, WAIT_FOREVER);
  }
  
  @Test @CAlways("INT") @CNever("DONE")
  public final void testInterruptDuringWait() {
    Thread t = new Thread(WAIT_FOREVER);
    t.start();
    t.interrupt();
  }
  
  @Test @CAlways("DONE") @CNever("INT")
  public final void testSpuriousWakeupDuringWait() {
    Thread t = new Thread(WAIT_FOREVER);
    t.start();
    while (t.getState() != Thread.State.WAITING) {  }
    CSystem.sendSpuriousWakeup(t);
  }
  
  @Test @CAlways("DONE") @CNever("INT")
  public final void testSpuriousWakeupAndInterruptDuringWaitCaseA() {
    Thread t = new Thread(WAIT_FOREVER);
    t.start();
    while (t.getState() != Thread.State.WAITING) {  }
    CSystem.sendSpuriousWakeup(t); 
    t.interrupt();
  }

  @Test @CAlways("INT") @CNever("DONE")
  public final void testSpuriousWakeupAndInterruptDuringWaitCaseB() {
    Thread t = new Thread(WAIT_FOREVER);
    t.start();
    while (t.getState() != Thread.State.WAITING) {  }
    t.interrupt(); // interrupt should be considered first
    CSystem.sendSpuriousWakeup(t); 
  }
  
  @SuppressWarnings("null")
  @Test(expected=NullPointerException.class)
  public final void testNullNotify() {
    Object o = null;
    o.notify();
  }

  @SuppressWarnings("null")
  @Test(expected=NullPointerException.class)
  public final void testNullNotifyAll() {
    Object o = null;
    o.notifyAll();
  }

  @SuppressWarnings("null")
  @Test(expected=NullPointerException.class)
  public final void testNullWait()  {
    Object o = null;
    try {
      o.wait();
    } catch(InterruptedException e) {

    }
  }

  @Test(expected=IllegalMonitorStateException.class)
  public final void testIllegalNotify() {
    new Object().notify();
  }

  @Test(expected=IllegalMonitorStateException.class)
  public final void testIllegalNotifyAll() {
    new Object().notifyAll();
  }

  @Test(expected=IllegalMonitorStateException.class)
  public final void testIllegalWait() {
    try {
      new Object().wait();
    } catch (InterruptedException e) { }
  }
  
  private static class Waiter implements Runnable {

    private final Data data;
    private final int threshold;

    Waiter(Data d, int v) {
      this.data = d;
      this.threshold = v;
    }
    public void run() {
      boolean didWait = false;
      synchronized(data) {
        while (data.x < threshold) {
          try {
            hotspot("WAIT");
            data.wait();
            didWait = true;
          } catch (InterruptedException e) { 
            hotspot("INT");
          }
        }
      }
      if (!didWait) {
        hotspot("NOWAIT");
      }
    }
  }

  private static class Notifier implements Runnable {
    private final Data data;
    private int count;
    private boolean notifyAll; 

    Notifier(Data d, int count, boolean notifyAll) {
      this.data = d;
      this.count = count;
      this.notifyAll = notifyAll;
    }

    public void run() {
      for (int i=1; i <= count; i++) {
        synchronized(data) {
          data.x = i;
          if (notifyAll) {
            data.notifyAll();
          } else {
            data.notify();
          }
        }
      }
    }
  }
  @Test @CSometimes({"WAIT","NOWAIT"}) @CNever("INT")
  public final void testPairedNotifyAndWait() {    
    Data d = new Data();
    CSystem.forkAndJoin(new Notifier(d,1,false), new Waiter(d, 1));
  }

  @Test @CSometimes({"WAIT","NOWAIT"}) @CNever("INT")
  public final void testPairedNotifyAllAndWait() {    
    Data d = new Data();
    CSystem.forkAndJoin(new Notifier(d,1,true), new Waiter(d, 1));
  }

  @Test @CSometimes({"WAIT","NOWAIT"}) @CNever("INT")
  public final void testPairedNotifyAllAndTwoWaiters() {    
    Data d = new Data();
    CSystem.forkAndJoin(new Notifier(d,2,true), new Waiter(d, 1), new Waiter(d, 2));
  }

  @Test @CSometimes({"WAIT","NOWAIT"}) @CNever("INT")
  public final void testPairedNotifyAllAndFourWaitersCaseA() {    
    Data d = new Data();
    CSystem.forkAndJoin(new Notifier(d,4,true), new Waiter(d, 1), new Waiter(d,1), new Waiter(d, 2), new Waiter(d, 2));
  }
  @Test @CSometimes({"WAIT","NOWAIT"}) @CNever("INT")
  public final void testPairedNotifyAllAndFourWaitersCaseB() {    
    Data d = new Data();
    CSystem.forkAndJoin(new Notifier(d,4,true), new Waiter(d, 1), new Waiter(d,1), new Waiter(d, 3), new Waiter(d, 4));
  }

  private static final Runnable[]
      rPairedNotifyAndTimedWait = {
    new Runnable() {
      public void run() {
        synchronized(SHARED_DATA) {
          SHARED_DATA.x = 1;
          SHARED_DATA.notify();
        }
      }
    },
    new Runnable() {
      public void run() {
        synchronized (SHARED_DATA) {
          try {
            long t = System.nanoTime();
            if (SHARED_DATA.x == 0) {
              SHARED_DATA.wait(0,1);

              if (SHARED_DATA.x == 0) {
                t = System.nanoTime() - t;
                assertTrue(t >= 1);
                hotspot("not notified");
              } else {
                hotspot("notified");
              }
            }
          } catch (InterruptedException e) {
            fail("Unexpected interruption");
          }
        }
      }
    }
  };
  @Test @CSometimes({"notified", "not notified"}) @Ignore
  public final void testPairedNotifyAndTimedWait() {
    SHARED_DATA = new Data();
    CSystem.forkAndJoin(rPairedNotifyAndTimedWait);
  }



  private static final Runnable[]
      rPairedNotifyAllAndTimedWait = {
    new Runnable() {
      public void run() {
        synchronized(SHARED_DATA) {
          SHARED_DATA.x = 1;
          SHARED_DATA.notifyAll();
        }
      }
    },
    new Runnable() {
      public void run() {
        synchronized (SHARED_DATA) {
          try {
            long t = System.nanoTime();
            if (SHARED_DATA.x == 0) {
              SHARED_DATA.wait(0,1);

              if (SHARED_DATA.x == 0) {
                t = System.nanoTime() - t;
                assertTrue(t >= 1);
                hotspot("not notified");
              } else {
                hotspot("notified");
              }
            }
          } catch (InterruptedException e) {
            fail("Unexpected interruption");
          }
        }
      }
    }
  };
  @Test @CSometimes({"notified", "not notified"}) @Ignore
  public final void testPairedNotifyAllAndTimedWait() {
    SHARED_DATA = new Data();
    CSystem.forkAndJoin(rPairedNotifyAllAndTimedWait);
  }

  public static final Runnable[]
      rPairedInterruptAndWait = {
    new Runnable() {
      public void run() {
        synchronized(SHARED_DATA) {
          SHARED_DATA.x = 1;
          CRuntime.getThread(1).interrupt();
        }
      }
    },
    new Runnable() {
      public void run() {
        synchronized (SHARED_DATA) {
          if (SHARED_DATA.x == 0) {
            try {
              hotspot("wait");
              SHARED_DATA.wait();
              hotspot("after wait");
            } catch (InterruptedException e) {

            }
          } else {
            hotspot("no wait");
          }
        }
      }
    }
  };

  @Test @Ignore
  @CSometimes({ "wait","no wait" }) 
  @CNever("after wait")
  public final void testPairedInterruptAndWait() {
    SHARED_DATA = new Data();
    CSystem.forkAndJoin(rPairedInterruptAndWait);
  }
}
