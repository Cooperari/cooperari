package org.cooperari.attic;

import java.util.concurrent.atomic.AtomicInteger;

public class Noise {
  public static AtomicInteger v = new AtomicInteger(); 

  public static void setCount(int n) {
    v.set(n); 
  }
  
  private static final Object SHARED_LOCK = new Object();
  private static int SHAREDFIELD;
  public static void tick() {
    synchronized(SHARED_LOCK) {
      for (SHAREDFIELD=0; SHAREDFIELD < 1000000; SHAREDFIELD++) { }
    }
  }
}
