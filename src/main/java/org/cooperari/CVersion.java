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
package org.cooperari;

/**
 * Cooperari version information.
 *
 * @since 0.2
 */
public class CVersion {
  
  /**
   * Cooperari version.
   */
  public static final String ID = "0.5-SNAPSHOT";
  
  /**
   * Java version used for building current version.
   */
  public static final String JAVA_BUILD_VERSION = "1.8.0_181";
  
  /**
   * Java VM name used for building current version.
   */
  public static final String JAVA_VM_BUILD_VERSION = "Java HotSpot(TM) 64-Bit Server VM";
  
  /**
   * Maven version used for building current version.
   */
  public static final String MAVEN_BUILD_VERSION = "3.2.5";
  
  /**
   * Operating system for building current version.
   */
  public static final String OS_BUILD_VERSION = "Mac OS X 10.16 x86_64";
  
  /**
   * Private constructor to avoid instantiation.
   */
  private CVersion() { }
   
}
