package org.cooperari.tools.cjavac;

import java.util.LinkedList;

/**
 * Main class for the <code>cjavac</code> utility program.
 * 
 * @since 0.2
 */
public class Main {

  /**
   * Private constructor to avoid instantiation.
   */
  private Main() {
  }

  /**
   * Program entry point. 
   * 
   * @param args Arguments.
   */
  @SuppressWarnings("deprecation")
  public static void main(String[] args) {
    String cooperariClassPath = System.getenv("COOPERARI_CLASSPATH");
    if (cooperariClassPath == null) {
      System.err.println("COOPERARI_CLASSPATH is not defined!");
      System.exit(1);
    }
    LinkedList<String> revisedArgs = new LinkedList<>();
    boolean set = false;
    for(int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.equals("-cp")) {
        arg += args[++i] + ":" + cooperariClassPath;
        set = true;
      }
      revisedArgs.add(arg);
    }
    if (!set) {
      String classPath = System.getenv("CLASSPATH");
      classPath = classPath == null ? cooperariClassPath : classPath + ":" + cooperariClassPath;
      revisedArgs.addFirst(classPath);
      revisedArgs.addFirst("-cp");
    }
    sun.tools.javac.Main.main(revisedArgs.toArray(new String[revisedArgs.size()]));
  }
}