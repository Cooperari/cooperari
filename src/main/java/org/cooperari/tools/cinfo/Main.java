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
