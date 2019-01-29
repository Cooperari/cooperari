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

package org.cooperari.sanity.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.cooperari.core.util.AgentLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class AgentLoaderTest {

  private static final File AGENT_JAR_FILE = new File("TestAgent.jar");
  private static final Class<?> AGENT_CLASS = DummyAgent.class;

  @BeforeClass
  public static void globalSetup() throws Throwable {
    try {
      Manifest manifest = new Manifest();
      Attributes attrs = manifest.getMainAttributes();
      
      attrs.put(Attributes.Name.MANIFEST_VERSION,"1.0");
      attrs.put(new Attributes.Name("Agent-Class"), AGENT_CLASS.getCanonicalName());
      String uri =  AGENT_CLASS.getPackage().getName().replace('.', '/')
          + '/' + AGENT_CLASS.getSimpleName() + ".class";
      InputStream is = AGENT_CLASS.getClassLoader().getResourceAsStream(uri);
      JarOutputStream jos = new JarOutputStream(new FileOutputStream(AGENT_JAR_FILE), manifest);
      jos.putNextEntry(new JarEntry(uri));
      while (is.available() > 0) {
        jos.write(is.read());
      }
      jos.closeEntry();
      jos.close();
    } catch(Throwable e) {
      AGENT_JAR_FILE.delete();
      throw e;
    }
  }

  @AfterClass
  public static void globalTeardown() throws Exception {
    AGENT_JAR_FILE.delete();
  }

  @Test
  public void test() throws Throwable {
    AgentLoader.load(AGENT_JAR_FILE.getAbsolutePath(), "");
    assertTrue("Agent has been loaded ...", DummyAgent.isLoaded());
  }

}
