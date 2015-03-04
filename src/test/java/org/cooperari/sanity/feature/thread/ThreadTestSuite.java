package org.cooperari.sanity.feature.thread;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ 
  ThreadInterruptionTest.class, 
  ThreadSleepTest.class,
  ThreadStopTest.class, 
  ThreadStartTest.class,
  ThreadJoinTest.class })
public class ThreadTestSuite {

}
