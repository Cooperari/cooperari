package org.cooperari.feature.data;

import static org.cooperari.core.CRuntime.getRuntime;

import org.cooperari.core.CThread;

/**
 * Data write operation.
 * @author Eduardo Marques, DI/FCUL, 2014-15
 */
public final class Write extends DataOperation {

  /**
   * Constructs the operation.
   * @param object Object.
   * @param key Data key.
   */
  public Write(Object object, Object key) {
    super(object, key);
    if (object != null) {
      RaceDetector rd = getRuntime().get(RaceDetector.class);
      if (rd != null) {
        rd.beginWrite(object, key);
      }
    }
  }

  /**
   * Yield on data write.
   * @param thisThread Current thread.
   * @param object Object.
   * @param key Data key.
   */
  public static void before(CThread thisThread, Object object, Object key) {
    thisThread.cYield(new Write(object, key));
  }
  
  /**
   * Execute actions after data write without yielding.
   * @param thisThread Current thread.
   * @param object Object.
   * @param key Data key.
   */
  public static void after(CThread thisThread, Object object, Object key) {
    RaceDetector rd = getRuntime().get(RaceDetector.class);
    if (rd != null) {
      rd.endWrite(object, key);
    }
  }
}
