package org.cooperari.sanity.feature.hotspot;


import static org.cooperari.CSystem.cHotspot;

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
        cHotspot("x");
    } 
  };
  
  @Test @CAlways("x")
  public void test1() {
    REACH = true;
    CSystem.cRun(rReach);
  }
  
  @Test 
  @CSometimes("x")
  public void test2() {
    REACH = true;
    CSystem.cRun(rReach);
  }
  
  @Test 
  @CNever("x")
  public void test3() {
    REACH = false;
    CSystem.cRun(rReach);
  }
  
  @Test(expected=CHotspotError.class) 
  @CAlways("x") 
  public void test4() {
    REACH = false;
    CSystem.cRun(rReach);
  }
  
  @Test(expected=CHotspotError.class)  
  @CNever("x")
  public void test6() {
    REACH = true;
    CSystem.cRun(rReach);
  }
}
