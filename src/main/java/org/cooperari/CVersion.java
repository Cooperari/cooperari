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
  public static final String ID = "0.2.1-SNAPSHOT";
  
  /**
   * Java version used for building current version.
   */
  public static final String JAVA_BUILD_VERSION = "1.8.0_20";
  
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
  public static final String OS_BUILD_VERSION = "Mac OS X 10.9.5 x86_64";
  
  /**
   * Private constructor to avoid instantiation.
   */
  private CVersion() { }
   
}
