package org.cooperari.sanity.feature.thread;

import static org.cooperari.CSystem.cHotspot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.cooperari.CSystem;
import org.cooperari.config.CAlways;
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
public class ThreadJoinTest {

  @Test(expected=WaitDeadlockError.class)
  public void testSelfJoin() {
    try {
      Thread.currentThread().join();
    } catch (InterruptedException e) {
      fail("unexpected interrupt");
    }  
  }
  
  private static class TestThread extends Thread {
    int value;

    TestThread(int v) {
      value = v;
    }

    @Override
    public void run() {
      if (value > 0) {
        Thread t = new TestThread(value-1);
        t.start();
        try {
          t.join();
        } catch (InterruptedException e) {
          fail("unexpected interrupt");
        }
      }
      hotspot("run " + value);
    }
  }

  private void seqTest(int n) {
    Thread t = new TestThread(n);
    t.start();
    try {
      t.join();
    } catch (InterruptedException e) {
      fail("unexpected interrupt");
    }
  }
  
  @Test @CAlways({"run 0"})
  public final void testSeq0() {
    seqTest(0);
  }
  @Test @CAlways({"run 0", "run 1"})
  public final void testSeq1() {
    seqTest(1);
  }

  @Test @CAlways({"run 0", "run 1", "run 2", "run 3"})
  public final void testSeq5() {
    seqTest(3);
  }

  private static class  MasterThread extends Thread {
    private int count;
    MasterThread(int n) {
      count = n;
    }
    public void run() {
      SlaveThread[] slaves = new SlaveThread[count];
      for (int i = 0; i < count; i++) {
        slaves[i] = new SlaveThread(i);
        slaves[i].start();
      }
      for (int i = 0; i < count; i++) {
        Data token = slaves[i].getToken();
        synchronized ( token ) {
          token.notify();
        }
      }
    }
  }


  private static class  SlaveThread extends Thread {
    Data token;

    SlaveThread(int id) {
      super("Slave " + id);
      token = new Data();
    }

    Data getToken() {
      return token;
    }

    @Override
    public void run() {
      while (true) {
        synchronized(token) {
          try {
            token.wait(); 
            token.x++; 
          } catch (InterruptedException e) {
            break;
          }

        }

      }
      assertEquals(1, token.x);
    }
  }
  @Test @Ignore
  public void testMSOneSlave() {
    CSystem.forkAndJoin(new MasterThread(2));
  }
}
