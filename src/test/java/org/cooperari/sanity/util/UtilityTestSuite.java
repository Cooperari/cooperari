package org.cooperari.sanity.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ 
   AgentLoaderTest.class, 
   CRawTupleTest.class,
   CResourceGraphTest.class, 
   UnsafeVMOperationsTest.class })
public class UtilityTestSuite {

}
