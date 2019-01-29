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

package org.cooperari.core.util;

import java.lang.management.ManagementFactory;

import com.sun.tools.attach.VirtualMachine;

/**
 * Utility class to load a Java agent programmatically.
 * 
 * @since 0.2
 */
public final class AgentLoader {

  /**
   * Load agent at runtime.
   * @param jarFilePath Agent jar file.
   * @param agentOptions Agent options.
   * @throws Throwable If an error occurs during agent loading or startup.
   * @see VirtualMachine#loadAgent(String, String)
   */
  public static void load(String jarFilePath, String agentOptions) throws Throwable {
    String vmName = ManagementFactory.getRuntimeMXBean().getName();
    String vmId = vmName.substring(0, vmName.indexOf('@'));
    VirtualMachine vm = VirtualMachine.attach(vmId);
    vm.loadAgent(jarFilePath, agentOptions);
    vm.detach();
  }

  @SuppressWarnings("javadoc")
  private AgentLoader() { }

}
