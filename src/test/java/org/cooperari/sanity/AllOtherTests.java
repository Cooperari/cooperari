package org.cooperari.sanity;

import org.cooperari.sanity.misc.MiscTestSuite;
import org.cooperari.sanity.util.UtilityTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ UtilityTestSuite.class, MiscTestSuite.class })
public class AllOtherTests {

}
