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


  @SuppressWarnings("unused")
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
