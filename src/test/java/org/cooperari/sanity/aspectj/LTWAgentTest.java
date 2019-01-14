package org.cooperari.sanity.aspectj;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.cooperari.core.aspectj.AspectCompiler;
import org.junit.AfterClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class LTWAgentTest {

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    
  }


  private static void compile(Class<?>... aspects) throws IOException, Error {
    File a = File.createTempFile(LTWAgentTest.class.getSimpleName(), ".jar");
    File b = File.createTempFile(LTWAgentTest.class.getSimpleName(), ".jar");
    ArrayList<String> msgs = new ArrayList<>();
    if (! AspectCompiler.compile(Arrays.asList(aspects), "", a, b, msgs)) {
      throw new Error("Unexpected AspectJ compiler error");
    }
   
  }
  @Test
  public void test() {
    fail("Not yet implemented");
  }

}
