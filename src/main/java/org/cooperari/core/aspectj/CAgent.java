package org.cooperari.core.aspectj;

import java.lang.instrument.Instrumentation;

/**
 * Cooperari agent.
 * 
 * The agent is loaded programmatically at runtime to perform load-time
 * weaving. It is a wrapper to the AspectJ load-time weaving agent.
 * 
 * @since 0.2
 * @see org.cooperari.core.util.AgentLoader
 *
 */
public final class CAgent {

  /**
   * "Pre-main" hook.
   * @param args Arguments.
   * @param inst Instrumentation handle
   * @throws Throwable any error during agent execution.
   */
  public static void premain(String args, Instrumentation inst) throws Throwable {
  
  }

  /**
   * "Agent-main" hook.
   * @param args Arguments.
   * @param inst Instrumentation handle
   * @throws Throwable any error during agent execution.
   */
  public static void agentmain(String args, Instrumentation inst) throws Throwable {
 
    
  }
  /**
   * Private constructor to prevent instantiation.
   */
  private CAgent() {
    
  }
  
}
