package org.cooperari.sanity.feature.thread;

import static org.cooperari.CSystem.cHotspot;
import static org.junit.Assert.fail;

import org.cooperari.CSystem;
import org.cooperari.config.CAlways;
import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({"javadoc","deprecation"})
@RunWith(CJUnitRunner.class) 
public class ThreadStopTest {

  @Test @CAlways("suicide")
  public final void testSuicide() {
    CSystem.forkAndJoin(new Runnable() {
      public void run() {
        try {
          Thread.currentThread().stop();
          fail("impossible");
        }
        catch(ThreadDeath e) {
          // OK
          hotspot("suicide");
          throw e;
        }
      }
    });
  }

  private static class Sleeper extends Thread {
    String id;
    boolean isRunning;
    Sleeper(String id) {
      this.id = id;
    }
    @Override
    public void run() {
      try {
        isRunning = true;
        while(true) {
          try { 
            Thread.sleep(100000);
          } catch(InterruptedException e) {
            fail("unexpected InterruptedException");
          }
        }
      } catch(ThreadDeath e) {
        hotspot("death"+id);
        // ok !
        throw e;
      }
    }
  };



  @Test @CAlways("deathA")
  public final void testStopFromOtherThread1() {
    Sleeper t = new Sleeper("A");
    t.start();
    while(!t.isRunning) {} 
    t.stop();
  }


  @Test @CAlways({"deathA","deathB"})
  public final void testStopFromOtherThread2() {
    Sleeper t1 = new Sleeper("A");
    Sleeper t2 = new Sleeper("B");
    t1.start();
    t2.start();
    while(!t1.isRunning) {} 
    while(!t2.isRunning) {}
    t1.stop();
    t2.stop();
  }

}
