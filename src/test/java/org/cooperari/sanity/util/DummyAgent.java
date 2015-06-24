package org.cooperari.sanity.util;

import java.lang.instrument.Instrumentation;


@SuppressWarnings("javadoc")
public class DummyAgent {

  private static boolean LOADED = false;
  
  public static void premain(String args, Instrumentation inst) throws Exception {
    LOADED = false;
  }

  public static void agentmain(String args, Instrumentation inst) throws Exception {
    LOADED = true;
  }

  public static boolean isLoaded() {
    return LOADED;
  }
}
