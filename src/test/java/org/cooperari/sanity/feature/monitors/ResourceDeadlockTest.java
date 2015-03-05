package org.cooperari.sanity.feature.monitors;

import static org.cooperari.CSystem.cHotspot;

import org.cooperari.CSometimes;
import org.cooperari.CSystem;
import org.cooperari.feature.monitor.CResourceDeadlockError;
import org.cooperari.junit.CJUnitRunner;
import org.cooperari.sanity.feature.Data;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
public class ResourceDeadlockTest {

  private static Data A = new Data();
  private static Data B = new Data();


  private static final Runnable[] rDeadlockPair = {
    new Runnable() {
      public void run() {
        synchronized (A) {
          try {
            synchronized (B) { }
          } catch(CResourceDeadlockError e) {
            cHotspot("deadlock");
          }
        }
      }
    },
    new Runnable() { 
      public void run() {
        synchronized (B) {
          try {
            synchronized (A) { }
          } catch(CResourceDeadlockError e) {
            cHotspot("deadlock");
          }
        }
      }
    }
  };

  @Test @CSometimes({"deadlock"})
  public final void testDeadlock1() {
    CSystem.cRun(rDeadlockPair);
  }

  private static final Runnable[] 
      rDeadlockForSure = {
    new Runnable() {
      public void run() {
        synchronized (A) {
          while (A.x == 0) { }
          try {
            synchronized (B) { B.x = 1; }
          } catch (CResourceDeadlockError e) {
            cHotspot("deadlock1");
          }
        }
      } 
    },
    new Runnable() { 
      public void run() {
        synchronized (B) {
          A.x = 1;
          try {
            synchronized (A) { }
          } catch (CResourceDeadlockError e) {
            cHotspot("deadlock2");
          }
        }
      }
    }
  };
  @Test @CSometimes({"deadlock1", "deadlock2"})
  public final void testDeadlock2() {
    A.x = 0;
    B.x = 0;
    CSystem.cRun(rDeadlockForSure);
  }
}
