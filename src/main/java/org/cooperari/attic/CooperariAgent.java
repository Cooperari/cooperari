package org.cooperari.attic;

import java.lang.instrument.Instrumentation;

import org.aspectj.weaver.loadtime.Agent;
import org.cooperari.CVersion;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.sun.tools.attach.VirtualMachine;

/**
 * Cooperari agent that is loaded dynamically at runtime.
 * 
 * <p>
 * The agent is simply a facade to AspectJ's weaver agent (
 * {@link org.aspectj.weaver.loadtime.Agent}) that cannot simply be loaded
 * programmatically (the <code>agentmain</code> method is not available in
 * AspectJ's agent).
 * </p>
 * 
 * <p><b>Implementation note</b>: this code is based 
 * on  <a href="http://dhruba.name/2010/02/07/creation-dynamic-loading-and-instrumentation-with-javaagents">
 * a tutorial by Dhruba Bandopadhyay</a>.
 * 
 * @since 0.2
 * @see org.aspectj.weaver.loadtime.Agent
 * @see java.lang.instrument.Instrumentation
 */
public class CooperariAgent {

  /**
   * JVM hook to load the agent at runtime.
   * 
   * @param args Arguments.
   * @param inst Instrumentation handle.
   * @throws Exception if an error occurs
   */
  public static void agentmain(String args, Instrumentation inst)
      throws Exception {
    org.aspectj.weaver.loadtime.Agent.premain(args, inst);
  }

  /**
   * Initialize the agent dynamically at runtime.
   */
  public static void initialize() {
    if (Agent.getInstrumentation() == null) {
      RuntimeMXBean vmBean = ManagementFactory.getRuntimeMXBean();
      String vmName = vmBean.getName();
      String pid = vmName.substring(0, vmName.indexOf('@'));

      try {
        String jarFilePath = 
            System.getenv("COOPERARI_HOME")
            + "/lib/cooperari-"+ CVersion.ID +".jar";
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(jarFilePath, "");
        vm.detach();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

}
