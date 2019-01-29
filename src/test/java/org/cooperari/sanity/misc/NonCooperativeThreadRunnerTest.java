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

package org.cooperari.sanity.misc;

import static org.junit.Assert.fail;

import org.cooperari.CSystem;
import org.cooperari.errors.CWaitDeadlockError;
import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
public class NonCooperativeThreadRunnerTest {

  // TODO: reconvert test
  @Test(expected = CWaitDeadlockError.class) @Ignore
  public final void testWaitDealock() {
    CSystem.forkAndJoin(new Runnable() {
      public void run() {
        Object o = new Object();
        synchronized (o) {
          try {
            o.wait();
          } catch (Exception e) {
            fail("impossible");
          }
        }
      }
    });
  }
}
