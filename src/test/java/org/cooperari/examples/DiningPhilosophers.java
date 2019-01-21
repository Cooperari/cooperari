
package org.cooperari.examples;

import static org.junit.Assert.*;

import org.cooperari.CSystem;
import org.cooperari.junit.CJUnitRunner;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Dining philosophers example.
 * 
 * @since 0.2
 */
@RunWith(CJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({"javadoc"})
public class DiningPhilosophers {

  static final int N_PHILOSOPHERS = 4;

  // The fork class.
  static class Fork { }
  
  // The Philosopher thread.
  static class Philosopher extends Thread {
    private final Fork leftFork, rightFork;
    private boolean hasEaten;
    public Philosopher(Fork left, Fork right) {
      leftFork = left;
      rightFork = right;
      hasEaten = false;
    }
    @Override
    public void run() {
      synchronized(leftFork) {
        synchronized(rightFork) {
          hasEaten = true;
        }
      }
    }
    public boolean hasEaten() {
      return hasEaten;
    }
  }
  
  // Philosopher array.
  Philosopher[] thePhilosophers;
  
  // Per-test setup
  @Before
  public void createThePhilosophers() {
    thePhilosophers = new Philosopher[N_PHILOSOPHERS];
    Fork[] forks = new Fork[N_PHILOSOPHERS];
    for (int i = 0; i < N_PHILOSOPHERS; i++) {
      forks[i] = new Fork();
    }
    for (int i = 0; i < N_PHILOSOPHERS; i++) {
      Fork left = forks[i];
      Fork right = forks[(i+1) % N_PHILOSOPHERS];
      thePhilosophers[i] = new Philosopher(left, right);
    }
  }
  
  // Verbose test 
  @Test
  public void testVariant1() throws InterruptedException {
    // Start philosophers
    for (Philosopher p : thePhilosophers) {
      p.start();
    }
    // Wait for them to terminate -- maybe :)
    for (Philosopher p : thePhilosophers) {
      p.join();
    }
    // Assert that all of them have eaten.
    for (Philosopher p : thePhilosophers) {
      assertTrue(p.hasEaten());
    }
  }
  
 // Concise test 
 @Test
 public void testVariant2() throws InterruptedException {
   CSystem.forkAndJoin(thePhilosophers);
   for (Philosopher p : thePhilosophers) {
     assertTrue(p.hasEaten());
   }
 }

}
