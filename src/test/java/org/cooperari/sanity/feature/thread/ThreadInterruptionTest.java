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
