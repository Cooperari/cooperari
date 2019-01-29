//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.cooperari.sanity.feature.thread;

import static org.cooperari.CSystem.hotspot;
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
