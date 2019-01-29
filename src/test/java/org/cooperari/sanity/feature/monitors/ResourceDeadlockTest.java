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

package org.cooperari.sanity.feature.monitors;

import static org.cooperari.CSystem.hotspot;

import org.cooperari.CSystem;
import org.cooperari.config.CSometimes;
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
            hotspot("deadlock");
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
            hotspot("deadlock");
          }
        }
      }
    }
  };

  @Test @CSometimes({"deadlock"})
  public final void testDeadlock1() {
    CSystem.forkAndJoin(rDeadlockPair);
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
            hotspot("deadlock1");
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
            hotspot("deadlock2");
          }
        }
      }
    }
  };
  @Test @CSometimes({"deadlock1", "deadlock2"})
  public final void testDeadlock2() {
    A.x = 0;
    B.x = 0;
    CSystem.forkAndJoin(rDeadlockForSure);
  }
}
