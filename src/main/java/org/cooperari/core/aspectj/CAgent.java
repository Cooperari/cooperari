//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

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
