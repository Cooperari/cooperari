package org.cooperari.sanity.feature.monitors;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ MonitorLockingTest.class, ResourceDeadlockTest.class,
    WaitAndNotifyTest.class })
public class MonitorTestSuite {

}
