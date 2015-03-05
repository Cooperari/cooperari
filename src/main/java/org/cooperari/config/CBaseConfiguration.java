package org.cooperari.config;

import org.cooperari.CAlways;
import org.cooperari.CNever;
import org.cooperari.CSometimes;

/**
 * Default configuration for test execution.
 * 
 * @since 0.2
 */
@CInstrument()
@CCoverage(CCoverageType.HDC)
@CMaxTrials(20)
@CRaceDetection(false)
@CDetectResourceDeadlocks()
@CAlways({}) 
@CNever({}) 
@CSometimes({})
@CTraceOptions()
@CTimeLimit()
public final class CBaseConfiguration {
  
  /**
   * Private constructor to prevent instantiation.
   */
  private CBaseConfiguration() { }
}
