package org.cooperari;

/**
 * Yield point for a thread.
 *
 * @since 0.2
 */
public interface CYieldPoint extends Comparable<CYieldPoint>{

  /**
   * Lock acquisition (<code>monitorenter</code>) signature.
   */
  String MONITOR_ENTER_SIGNATURE = "<monitorenter>";
  
  /**
   * Lock acquisition (<code>monitorexit</code>) signature.
   */
  String MONITOR_EXIT_SIGNATURE = "<monitorexit>";

  /**
   * Get signature. This represents the point of execution
   * that caused the thread yield.
   * @return The signature for the yield point.
   */
  public String getSignature();
  
  /**
   * Get source file name.
   * @return The source file for the weave point.
   */
  String getSourceFile();
  

  /**
   * Get source code line.
   * @return The source code line for the weave point.
   */
  int getSourceLine();
 
  
}
