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

package org.cooperari.app;

import org.cooperari.CTestResult;
import org.cooperari.core.CSession;
@SuppressWarnings("javadoc")
public class CApplicationRunner {

  private final CApplication _app;
  CApplicationRunner(CApplication app) {
    _app = app;
  }
  public void run() {

    CTestResult result = CSession.executeTest(_app);
    System.out.println(result);
//    if (result.failed()) {
//      notifier.fireTestFailure(new Failure(desc, result.getFailure()));
//    } else {
//      notifier.fireTestFinished(desc);
//    }
  }
}
