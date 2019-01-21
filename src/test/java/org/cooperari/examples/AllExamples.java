package org.cooperari.examples;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ 
  DiningPhilosophers.class,
  SimpleRaceDetection.class,
  BadCounterUsingAtomicPrimitives.class,
  BuggySemaphoreImplementation.class, 
})
public class AllExamples {

}
