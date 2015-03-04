package org.cooperari.sanity.misc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ CConfigurationTest.class, CUncaughtExceptionHandlerTest.class, NonCooperativeThreadRunnerTest.class })
public class MiscTestSuite {

}
