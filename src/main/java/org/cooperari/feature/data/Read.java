package org.cooperari.feature.data;

import static org.cooperari.core.CRuntime.getRuntime;

import org.cooperari.core.CThread;

/**
 * Data read operation.
 * 
 * @author Eduardo Marques, DI/FCUL, 2014-15
 */
public final class Read extends DataOperation {

  /**
   * Constructs the operation.
   * @param object Object.
   * @param key Data key.
   */
  private Read(Object object, Object key) {
    super(object, key);
    if (object != null) {
      RaceDetector rd = getRuntime().get(RaceDetector.class);
      if (rd != null) {
        rd.beginRead(object, key);
      }
    }
  }

  /**
   * Yield on data read.
   * @param thisThread Current thread.
   * @param object Object.
   * @param key Data key.
   */
  public static void before(CThread thisThread, Object object, Object key) {
    thisThread.cYield(new Read(object, key));
  }
  
  /**
   * Execute actions after data read without yielding.
   * @param thisThread Current thread.
   * @param object Object.
   * @param key Data key.
   */
  public static void after(CThread thisThread, Object object, Object key) {
    RaceDetector rd = getRuntime().get(RaceDetector.class);
    if (rd != null) {
      rd.endRead(object, key);
    }
  }
 
}
