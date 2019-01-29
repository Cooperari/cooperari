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

package org.cooperari.sanity;

import org.cooperari.sanity.feature.atomic.AtomicTestSuite;
import org.cooperari.sanity.feature.data.DataTestSuite;
import org.cooperari.sanity.feature.hotspot.HotspotTestSuite;
import org.cooperari.sanity.feature.junit.CJUnitRunnerSanityTestSuite;
import org.cooperari.sanity.feature.monitors.MonitorTestSuite;
import org.cooperari.sanity.feature.thread.ThreadTestSuite;
import org.cooperari.sanity.feature.threadrunner.ThreadRunnerSuite;
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
