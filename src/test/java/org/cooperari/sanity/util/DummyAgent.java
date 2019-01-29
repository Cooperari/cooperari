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
