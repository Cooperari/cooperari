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

package org.cooperari.sanity.feature.atomic;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.cooperari.CSystem;
import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
public class AtomicTest {

  @Test 
  public void test1() {
    executeTest(1);
  }

  @Test 
  public void test2() {
    executeTest(2);
  }

  @Test 
  public void test4() {
    executeTest(4);
  }

  @Test 
  public void test8() {
    executeTest(8);
  }

  private void executeTest(int n) {
    final AtomicBoolean token = new AtomicBoolean(true);
    final AtomicInteger nextValue = new AtomicInteger(0);
    final LinkedList<Integer> list = new LinkedList<>();
    Runnable[] r = new Runnable[n];
    for (int i = 0; i < n; i++) {
      r[i] = new Runnable() {
        public void run() {
          while(true) {
            if (token.compareAndSet(true, false)) {
              list.add(nextValue.getAndIncrement());
              token.set(true);
              break;
            }
          }
        }
      };
    }
    CSystem.forkAndJoin(r);
    assertEquals("list size", n, list.size());
    assertEquals("atomic integer value", n, nextValue.get());
    for (int i = 0; i < n ; i++) {
      assertEquals("position " + i, (Integer) i, list.get(i));
    }
  }

}