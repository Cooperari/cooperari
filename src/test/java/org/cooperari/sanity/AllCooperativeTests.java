package org.cooperari.sanity;

import org.cooperari.sanity.feature.atomic.AtomicTestSuite;
import org.cooperari.sanity.feature.data.DataTestSuite;
import org.cooperari.sanity.feature.hotspot.HotspotTestSuite;
import org.cooperari.sanity.feature.monitors.MonitorTestSuite;
import org.cooperari.sanity.feature.thread.ThreadTestSuite;
import org.cooperari.sanity.feature.threadrunner.ThreadRunnerSuite;
import org.cooperari.sanity.junit.CJUnitRunnerSanityTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ 
    CJUnitRunnerSanityTestSuite.class,
    HotspotTestSuite.class, 
    DataTestSuite.class,
    AtomicTestSuite.class, 
    MonitorTestSuite.class, 
    ThreadTestSuite.class,
    ThreadRunnerSuite.class
    })
public class AllCooperativeTests {

}
