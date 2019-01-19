package org.cooperari.tools.cinfo;

import static java.lang.System.getProperty;

import java.util.Map;
import java.util.TreeMap;

import org.cooperari.CVersion;

 
/**
 * Main class for the <code>cinfo</code> utility program.
 * 
 * @since 0.2
 */
public class Main {

  /**
   * Private constructor to avoid instantiation.
   */
  private Main() {
  }

  @SuppressWarnings("javadoc")
  private static String getOSInfo() {
    return getProperty("os.name") + ' ' + getProperty("os.version") + ' '
        + getProperty("os.arch");
  }

  @SuppressWarnings("javadoc")
  private static void dump(String desc, Map<?, ?> map) {
    System.out.println(desc);
    TreeMap<Object, Object> orderedMap = new TreeMap<>(map);
    for (Map.Entry<?, ?> p : orderedMap.entrySet()) {
      System.out.printf("%s: %s%n", p.getKey(), p.getValue());
    }
  }

  /**
   * Program entry point.
   * 
   * @param args Program arguments are ignored.
   */
  public static void main(String[] args) {
    System.out
        .println("== Cooperari version ==%n"
            + CVersion.ID
            + "%n== Build environment for this release =="
            + "%nJava version: "
            + CVersion.JAVA_BUILD_VERSION
            + "%nJava VM: "
            + CVersion.JAVA_VM_BUILD_VERSION
            + "%nMaven version: "
            + CVersion.MAVEN_BUILD_VERSION
            + "%nOS: "
            + CVersion.OS_BUILD_VERSION
            + "%n== Runtime environment :: main settings and dependency versions ==%n"
            + "OS: " 
            + getOSInfo() 
            + "%nJava version: " 
            + getProperty("java.version") 
            + "%nJava VM: " 
            + getProperty("java.vm.name") 
            + "%nJUnit version: "
            + junit.runner.Version.id()
            + "%nAspectJ version: "
            + org.aspectj.bridge.Version.text);
    dump("== Runtime environment :: Java properties ==", System.getProperties());
    dump("== Runtime environment :: environment variables ==", System.getenv());

  }
}
