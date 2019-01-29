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

package org.cooperari.sanity.feature.junit;

import static org.junit.Assert.assertEquals;

import org.cooperari.config.CMaxTrials;
import org.cooperari.junit.CJUnitRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({ "javadoc" })
@RunWith(CJUnitRunner.class)
public class CJUnitRunnerSanityTest1 {
  private static final int TEST_METHODS = 3;
  private static int setupBeforeClassCounter = 0;
  private static int setupCounter = Integer.MAX_VALUE;
  private static int tearDownCounter = Integer.MAX_VALUE;

  @BeforeClass
  public static void setUpBeforeClass() {
    setupBeforeClassCounter++;
    setupCounter = 0;
    tearDownCounter = 0;
  }

  @AfterClass
  public static void tearDownAfterClass() {
    assertEquals("setup counter", TEST_METHODS, setupCounter);
    assertEquals("teardown counter", TEST_METHODS, tearDownCounter);
    assertEquals("class setup counter", 1, setupBeforeClassCounter);
  }

  @Before
  public void setUp() throws Exception {
    setupCounter++;
  }

  @After
  public void tearDown() throws Exception {
    tearDownCounter++;
    assertEquals("setup vs. teardown", tearDownCounter, setupCounter);
  }

  @Test(expected = NullPointerException.class)
  @CMaxTrials(1)
  public void test1() {
    assertEquals("teardown counter", 0, tearDownCounter);
    assertEquals("setup counter", 1, setupCounter);
    throw new NullPointerException();
  }

  @Test
  @CMaxTrials(1)
  public void test2() {
    assertEquals("teardown counter", 1, tearDownCounter);
    assertEquals("setup counter", 2, setupCounter);
  }

  @Test
  @CMaxTrials(1)
  public void test3() {
    assertEquals("teardown counter", 2, tearDownCounter);
    assertEquals("setup counter", 3, setupCounter);
  }
}
