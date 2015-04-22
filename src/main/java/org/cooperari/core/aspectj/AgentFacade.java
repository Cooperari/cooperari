package org.cooperari.core.aspectj;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.bridge.IMessage;
import org.aspectj.lang.JoinPoint;
import org.cooperari.CYieldPoint;
import org.cooperari.core.CWorkspace;
import org.cooperari.core.CYieldPointImpl;
import org.cooperari.core.CoverageLog;
import org.cooperari.core.util.CLog;
import org.cooperari.core.util.CReport;

/**
 * Facade for the load-time weaving agent.
 * 
 * @since 0.2
 */
public enum AgentFacade {
  /**
   * Singleton instance (there is only one load-time weaver agent).
   */
  INSTANCE;

  // INSTANCE FIELDS
  /**
   * Indicates that the weaver agent is active.
   */
  private boolean _active = false;

  /**
   * Global coverage log.
   */
  private final CoverageLog _globalCoverageLog = new CoverageLog();

  /**
   * Test if weaver agent is active.
   * 
   * @return <code>true</code> if weaver agent is active.
   */
  public boolean isActive() {
    return _active;
  }

  /**
   * Signal activation. This method is (only) called by {@link AgentMessageHandler#AgentMessageHandler}, 
   * which in turn is instantiated (only) by the load-time weaver agent.
   */
  void signalActivation() {
    _active = true;
  }

  /**
   * Get total number of yield points recorded.
   * 
   * @return Yield point count.
   */
  public int getYieldPointCount() {
    return _globalCoverageLog.getYieldPointCount();
  }

  /**
   * Get total number of yield points covered.
   * 
   * @return Yield point count.
   */
  public int getYieldPointsCovered() {
    return _globalCoverageLog.getYieldPointsCovered();
  }

  /**
   * Compiled regular expression for {@link IMessage#WEAVEINFO} messages.
   */
  private static final Pattern WI_MSG_PATTERN = Pattern
      .compile("^Join point '([^\\(]+)\\([^ ]+ (.+)\\)' in Type '.+' \\((.+):([0-9]+)\\).+");

  /**
   * Log id.
   */
  private static final String LOG_ID = "aspectj-weaver";

  /**
   * Log handle.
   */
  private CLog _agentLog;

  /**
   * Handle weaver agent message.
   * 
   * @param msg Message object.
   */
  void handleMessage(IMessage msg) {

    if (CWorkspace.INSTANCE.isInitialized()) {
      if (_agentLog == null) {
        try {
          _agentLog = CWorkspace.INSTANCE.createLog(LOG_ID,
              CLog.Option.OMMIT_THREAD_INFO);
        } catch (Throwable e) {
          _agentLog = CLog.SYSTEM_OUT;
        }
      }
      _agentLog.message(msg.toString());
    }

    if (msg.getKind() == IMessage.WEAVEINFO) {
      try {
        Matcher matcher = WI_MSG_PATTERN.matcher(msg.getMessage());
        if (matcher.find()) {
          String kind = matcher.group(1);
          String desc = matcher.group(2);
          String signature;
          if (kind.equals(JoinPoint.SYNCHRONIZATION_LOCK)) {
            signature = CYieldPoint.MONITOR_ENTER_SIGNATURE;
          } else if (kind.equals(JoinPoint.SYNCHRONIZATION_UNLOCK)) {
            signature = CYieldPoint.MONITOR_EXIT_SIGNATURE;
          } else if (kind.equals(JoinPoint.METHOD_CALL)) {
            signature = uniformize(desc);
          } else {
            signature = kind + "(" + uniformize(desc) + ")";
          }
          String fileInfo = matcher.group(3);
          int lineInfo = Integer.parseInt(matcher.group(4));
          CYieldPointImpl yp = new CYieldPointImpl(signature, fileInfo,
              lineInfo);
          synchronized (this) {
            _globalCoverageLog.recordDefinition(yp);
          }
        }
      } catch (Throwable e) {
        handleError(e);
      }
    }
    // else {
    //
    // }
  }

  @SuppressWarnings("javadoc")
  private String uniformize(String s) {
    return s.replace("java.lang.", "").replace('$', '.');
  }

  /**
   * Get coverage rate.
   * 
   * @return The percentage of yield points covered.
   */
  public double getCoverageRate() {
    return _globalCoverageLog.getCoverageRate();
  }

  /**
   * Mark a set of yield points as covered and get an estimate of uncovered
   * yield points in the same source files as the set that were not covered.
   * The method also writes a coverage report to a file for the given set of yield points.
   * 
   * @param log Log containing only covered yield points.
   * @return Uncovered yield points.
   */
  public int enrichCoverageInfo(CoverageLog log) {
    synchronized(this) {
      if (log.getYieldPointCount() == 0) {
        return 0;
      }
      _globalCoverageLog.enrich(log, false);
      log.enrich(_globalCoverageLog, true);
      return log.getYieldPointCount() - log.getYieldPointsCovered();
    }
  }

  /**
   * Handle an error.
   * 
   * @param e Exception object.
   */
  private void handleError(Throwable e) {
    // Do nothing for now.
  }

  /**
   * Coverage log id.
   */
  private static final String COVERAGE_REPORT_ID = "coverage";

  /**
   * Generate coverage report.
   * 
   * @throws IOException if an I/O error occurs.
   * @return File object for the coverage report.
   */
  public File produceCoverageReport() throws IOException {
    synchronized (this) {
      return _globalCoverageLog.produceCoverageReport(COVERAGE_REPORT_ID);
    }
  }

}
