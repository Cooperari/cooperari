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

package org.cooperari.sanity.feature.hotspot;


import static org.cooperari.CSystem.hotspot;

import org.cooperari.CSystem;
import org.cooperari.config.CAlways;
import org.cooperari.config.CNever;
import org.cooperari.config.CSometimes;
import org.cooperari.errors.CHotspotError;
import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
public class HotspotSanityTest {
  private static boolean REACH = false;
  
  private static final Runnable rReach = new Runnable() {
    @Override
    public void run() {
      if (REACH)
        hotspot("x");
    } 
  };
  
  @Test @CAlways("x")
  public void test1() {
    REACH = true;
    CSystem.forkAndJoin(rReach);
  }
  
  @Test 
  @CSometimes("x")
  public void test2() {
    REACH = true;
    CSystem.forkAndJoin(rReach);
  }
  
  @Test 
  @CNever("x")
  public void test3() {
    REACH = false;
    CSystem.forkAndJoin(rReach);
  }
  
  @Test(expected=CHotspotError.class) 
  @CAlways("x") 
  public void test4() {
    REACH = false;
    CSystem.forkAndJoin(rReach);
  }
  
  @Test(expected=CHotspotError.class)  
  @CNever("x")
  public void test6() {
    REACH = true;
    CSystem.forkAndJoin(rReach);
  }
}
