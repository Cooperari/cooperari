package org.cooperari.examples;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ 
  DiningPhilosophers.class,
  RaceDetection.class,
  SemaphoreWithBug.class, 
  AtomicPrimitives.class,
  SunMiscUnsafe.class
})
public class All {

}
