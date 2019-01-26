package org.cooperari.examples;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite grouping all examples.
 * 
 * @since 0.2
 */
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
