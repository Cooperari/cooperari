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
