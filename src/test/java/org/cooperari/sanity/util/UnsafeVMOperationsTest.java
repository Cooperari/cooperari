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

package org.cooperari.sanity.util;

import static org.cooperari.core.util.UnsafeVMOperations.UNSAFE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({ "javadoc", "restriction", "deprecation" })
public class UnsafeVMOperationsTest {

  @Test
  public void testMonitor1() {
    Object o = new Object();
    UNSAFE.monitorEnter(o);
    boolean holdsLock1 = Thread.holdsLock(o);
    UNSAFE.monitorExit(o);
    boolean holdsLock2 = Thread.holdsLock(o);
    assertTrue("owner after UNSAFE.monitorEnter", holdsLock1);
    assertFalse("not owner after UNSAFE.monitorExit", holdsLock2);
  }

  @Test
  public void testMonitor2() {
    Object o = new Object();
    UNSAFE.monitorEnter(o);
    boolean holdsLock = Thread.holdsLock(o);
    boolean holdsLock2;
    synchronized (o) {
      holdsLock2 = Thread.holdsLock(o);
    }
    boolean holdsLock3 = Thread.holdsLock(o);
    UNSAFE.monitorExit(o);
    boolean holdsLock4 = Thread.holdsLock(o);
    assertTrue("owner after UNSAFE.monitorEnter", holdsLock);
    assertTrue("owner after entering synchronized block", holdsLock2);
    assertTrue("owner after leaving synchronized block", holdsLock3);
    assertFalse("not owner after UNSAFE.monitorExit", holdsLock4);
  }

  @Test
  public void testMonitor3() {
    Object o = new Object();
    boolean holdsLock1, holdsLock2, holdsLock3, holdsLock4;
    synchronized (o) {
      holdsLock1 = Thread.holdsLock(o);
      UNSAFE.monitorExit(o);
      holdsLock2 = Thread.holdsLock(o);
      UNSAFE.monitorEnter(o);
      holdsLock3 = Thread.holdsLock(o);
    }
    holdsLock4 = Thread.holdsLock(o);
    assertTrue("owner after entering synchronized block", holdsLock1);
    assertFalse("not owner after UNSAFE.monitorExit", holdsLock2);
    assertTrue("owner after UNSAFE.monitorExit", holdsLock3);
    assertFalse("not owner after leaving synchronized block", holdsLock4);
  }

}
