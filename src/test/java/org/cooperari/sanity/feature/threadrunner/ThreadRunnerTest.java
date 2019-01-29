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

package org.cooperari.sanity.feature.threadrunner;

import static org.junit.Assert.assertEquals;

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
public class ThreadRunnerTest {
  private void executeTest(final int n) {
    final AtomicInteger value = new AtomicInteger(0);
    CSystem.forkAndJoin(new Runnable() { 
      public void run() {
        Runnable r = new Runnable() {
          public void run() {
            value.getAndIncrement();
          }
        };
        final Runnable[] runnables = new Runnable[n];
        for (int i = 0; i < n; i++) {
          runnables[i] = r;
        }
        CSystem.forkAndJoin(runnables);
        assertEquals("expected value", n, value.get());
      }
    });
  }
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
}
