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
