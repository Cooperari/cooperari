package org.cooperari.sanity.misc;

import static org.junit.Assert.fail;

import org.cooperari.CCoverage;
import org.cooperari.CCoverageType;
import org.cooperari.CSystem;
import org.cooperari.core.WaitDeadlockError;
import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
@CCoverage(CCoverageType.NONE)
public class NonCooperativeThreadRunnerTest {

  // TODO: reconvert test
  @Test(expected = WaitDeadlockError.class) @Ignore
  public final void testWaitDealock() {
    CSystem.cRun(new Runnable() {
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
