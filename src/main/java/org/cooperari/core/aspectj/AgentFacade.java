package org.cooperari.core.aspectj;

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
   * Weave point map. It maps weave points to a boolean value indicating if the
   * weave point has been covered or not.
   */
  private final TreeMap<CYieldPoint, Boolean> _yieldPoints = new TreeMap<>();

  /**
   * Covered weave point count.
   */
  private int _coveredYieldPoints = 0;

  /**
   * Test if weaver agent is active.
   * 
   * @return <code>true</code> if weaver agent is active.
   */
  public boolean isActive() {
    return _active;
  }

  /**
   * Signal activation. This method is (only) called by
   * {@link AgentMessageHandler#AgentMessageHandler}.
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
    return _yieldPoints.size();
  }

  /**
   * Get total number of yield points covered.
   * 
   * @return Yield point count.
   */
  public int getYieldPointsCovered() {
    return _coveredYieldPoints;
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
   * Log handle
   */
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
            if (!_yieldPoints.containsKey(yp)) {
              _yieldPoints.put(yp, false);
            }
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
   * @return The percentage of weave points covered.
   */
  public int getCoverageRate() {
    return (_coveredYieldPoints * 100) / _yieldPoints.size();
  }

  /**
   * Mark a set of yield points as covered and get an estimate of uncovered
   * yield points in the same source files as the set that were not covered.
   * 
   * @param ypSet Set of yield points.
   * @return Uncovered yield points.
   */
  public int recordYieldPointsCovered(Set<CYieldPoint> ypSet) {
    synchronized(this) {
      if (ypSet.isEmpty()) {
        return 0;
      }

      // First update global coverage data 
      // and get list of source files to look for uncovered yield points.
      HashSet<String> sourceFiles = new HashSet<>();
      for (CYieldPoint yp : ypSet) {
        sourceFiles.add(yp.getSourceFile());
        synchronized(_yieldPoints) {
          Boolean b = _yieldPoints.put(yp, true);
          if (b == null || b == false) {
            // Mark yield point as globally covered
            _coveredYieldPoints ++;
          }
        }
      }
      // Now get number uncovered yield points in all source files
      int uncovered = 0;
      for (String sf : sourceFiles) {
        CYieldPointImpl lowerBound = new CYieldPointImpl("", sf, -1);
        // Note: extra char to force iteration until last possible entry
        CYieldPointImpl upperBound = new CYieldPointImpl("", sf + '*', -1);
        Map<CYieldPoint,Boolean> view = _yieldPoints.subMap(lowerBound, upperBound);
        for (CYieldPoint yp : view.keySet()) {
          if (!ypSet.contains(yp)) {
            uncovered++;
          }
        }
      }
      return uncovered;
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
   */
  public void produceCoverageReport() throws IOException {
    CReport r = CWorkspace.INSTANCE.createReport(COVERAGE_REPORT_ID);
    r.beginSection("GLOBAL STATISTICS", "TOTAL", "COVERED", "%");
    r.writeEntry(getYieldPointCount(), getYieldPointsCovered(),
        getCoverageRate());
    r.beginSection("WEAVE POINTS", "C", "SOURCE FILE", "LINE", "SIGNATURE");
    for (Entry<CYieldPoint, Boolean> e : _yieldPoints.entrySet()) {
      r.writeEntry(e.getValue() ? 'Y' : 'N', e.getKey().getSourceFile(), e
          .getKey().getSourceLine(), e.getKey().getSignature());
    }
    r.close();
  }

}
