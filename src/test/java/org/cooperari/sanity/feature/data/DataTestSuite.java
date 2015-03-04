package org.cooperari.sanity.feature.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ FieldAccessTest.class, FieldRaceDetectionTest.class, ArrayAccessTest.class, ArrayRaceDetectionTest.class })
public class DataTestSuite {

}
