package org.cooperari.feature.thread;

import org.cooperari.core.CThread;

/**
 * Cooperative operation for {@link Thread#yield()}.
 * 
 * @since 0.2
 */
public  final class Yield extends ThreadOperation<Void> {    
  /**
   * Constructor.
   */ 
  private Yield() {
    super();
  }
  
  /**
   * Singleton instance.
   */
  public final static Yield INSTANCE = new Yield();


  /**
   * Execute a thread yield operation.
   * @param thisThread This thread.
   */
  public static void execute(CThread thisThread) {
    thisThread.cYield(INSTANCE);
  }
}
