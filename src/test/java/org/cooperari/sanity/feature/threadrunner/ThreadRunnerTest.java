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
    CSystem.cRun(new Runnable() { 
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
        CSystem.cRun(runnables);
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
