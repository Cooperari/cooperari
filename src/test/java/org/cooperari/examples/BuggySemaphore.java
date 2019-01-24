package org.cooperari.examples;

import static org.junit.Assert.*;



import org.cooperari.CSystem;
import org.cooperari.config.CMaxTrials;
import org.cooperari.config.CRaceDetection;
import org.cooperari.config.CScheduling;
import org.cooperari.core.scheduling.CProgramStateFactory;
import org.cooperari.core.scheduling.CSchedulerFactory;
import org.cooperari.junit.CJUnitRunner;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(CJUnitRunner.class)
@CRaceDetection(value=true,throwErrors=false)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({"javadoc"})
public class BuggySemaphore {

  
  static class Semaphore {
    int value;
    Semaphore(int n) {
      value = n;
    }
    int value() {
      return value;
    }
    
    public void up() {
      synchronized(this) {
        value++;
        notifyAll();
      }
    }

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
  }
  
  @Test
  @CMaxTrials(1000)
  @CScheduling(schedulerFactory=CSchedulerFactory.OBLITUS, stateFactory=CProgramStateFactory.RAW)
  public void test1() {
    Semaphore s = new Semaphore(0);
    CSystem.forkAndJoin(
        () -> { s.up(); },
        () -> { s.up(); },
        () -> { s.down(); },
        () -> { s.down(); }
    );
    assertEquals(0, s.value);
  }
  
 
}


