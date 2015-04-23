package org.cooperari.config;


/**
 * Default configuration for test execution.
 * 
 * @since 0.2
 */
@CInstrument()
@CScheduling()
@CMaxTrials()
@CRaceDetection(false)
@CDetectResourceDeadlocks()
@CTimeLimit()
@CGenerateCoverageReports()
@CAlways({}) 
@CNever({}) 
@CSometimes({})
@CTraceOptions()
public final class CBaseConfiguration {
  
  /**
   * Private constructor to prevent instantiation.
   */
  private CBaseConfiguration() { }
}
