package org.cooperari.core.aspectj;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.bridge.IMessage;
import org.aspectj.lang.JoinPoint;
import org.cooperari.core.CWorkspace;
import org.cooperari.core.CYieldPoint;
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
  
 

  // CONSTANTS  
  /**
   * String identifying 'monitorenter' point-cut.
   */
  public static final String MONITOR_ENTER_JOINPOINT = "<monitorenter>";
  
  /**
   * String identifying 'monitorexit' point-cut.
   */
  public static final String MONITOR_EXIT_JOINPOINT = "<monitorexit>";
  
  // INSTANCE FIELDS
  /**
   * Indicates that the weaver agent is active.
   */
  private boolean _active = false;
  
  
  /**
   * Weave point map. It maps weave points to a boolean value indicating if the weave point has been covered or not. 
   */
  private final TreeMap<WeavePoint, Boolean> _weavePoints = new TreeMap<>();
  
  /**
   * Covered weave point count.
   */
  private int _coveredWeavePoints = 0;
  
  /**
   * Test if weaver agent is active.
   * @return <code>true</code> if weaver agent is active.
   */
  public boolean isActive() {
    return _active;
  }
  
  /**
   * Signal activation.
   * This method is (only) called by {@link AgentMessageHandler#AgentMessageHandler}. 
   */
  void signalActivation() {
    _active = true;    
  }
 
  /**
   * Get total number of weave points recorded.
   * @return Weave point count.
   */
  public int getWeavePointCount() {
    return _weavePoints.size();
  }
  
  /**
   * Get total number of weave points covered.
   * @return Weave point count.
   */
  public int getWeavePointsCovered() {
    return _coveredWeavePoints;
  }
  
  /**
   * Compiled regular expression for {@link IMessage#WEAVEINFO} messages.
   */
  private  static final Pattern WI_MSG_PATTERN =  Pattern.compile("^Join point '([^\\(]+)\\([^ ]+ (.+)\\)' in Type '.+' \\((.+):([0-9]+)\\).+");
 

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
   * @param msg Message object.
   */
  void handleMessage(IMessage msg) {
    
    if (CWorkspace.INSTANCE.isInitialized()) {
      if (_agentLog == null) {
        try {
          _agentLog = CWorkspace.INSTANCE.createLog(LOG_ID, CLog.Options.OMMIT_THREAD_INFO);
        }
        catch (Throwable e) {
          _agentLog = CLog.SYSTEM_OUT;
        }
      }
      _agentLog.message(msg.toString());
    }
    
    if (msg.getKind() == IMessage.WEAVEINFO) {
      try {
        Matcher matcher = WI_MSG_PATTERN.matcher(msg.getMessage());
        //System.out.println("F" + matcher.find() + " " + "M " + matcher.groupCount());
        if (matcher.find()) {
          String kind = matcher.group(1);
          String desc = matcher.group(2);


          String signature;
          if (kind.equals(JoinPoint.SYNCHRONIZATION_LOCK)) {
            signature = AgentFacade.MONITOR_ENTER_JOINPOINT;
          } else if (kind.equals(JoinPoint.SYNCHRONIZATION_UNLOCK)){
            signature = AgentFacade.MONITOR_EXIT_JOINPOINT;
          } else if (kind.equals(JoinPoint.METHOD_CALL)){
            signature = uniformize(desc);
          } else {
            signature = kind + "(" + uniformize(desc) + ")";
          }
          String fileInfo = matcher.group(3);
          int lineInfo = Integer.parseInt(matcher.group(4));
          WeavePoint wp = new AgentFacade.WeavePoint(fileInfo, lineInfo, signature);
          record(wp);
        }
      } catch(Throwable e) {
        handleError(e);
      }
    } 
//    else {
//      
//    }
  }
  
  @SuppressWarnings("javadoc")
  private String uniformize(String s) {
    return s.replace("java.lang.", "").replace('$','.');
  }
  
  /**
   * Record a weave point processed by the load-time weaver.
   * @param wp Weave point.
   */
  private void record(WeavePoint wp)  {
    Boolean b = _weavePoints.put(wp, false);
    if (b != null && b == true) {
      // this should not ever happen, but ... 
      _weavePoints.put(wp, true);
    }
  }
  
  /**
   * Check if a weave point has been recorded.
   * @param wp Weave point.
   * @return <code>true</code> if the weave point is recorded.
   */
  public boolean isRecorded(WeavePoint wp) {
    return _weavePoints.containsKey(wp);
  }
  
  /**
   * Check if a weave point has been marked as covered.
   * @param wp Weave point.
   * @return <code>true</code> if the weave point is recorded.
   */
  public boolean hasBeenCovered(WeavePoint wp) {
    Boolean b = _weavePoints.get(wp);
    return b != null && b == true;
  }
  
  /**
   * Get coverage rate.
   * @return The percentage of weave points covered.
   */
  public int getCoverageRate() {
    return (_coveredWeavePoints * 100) / _weavePoints.size();
  }

  /**
   * Mark location as reached.   
   *  
   * @param yp Yield point as represent by the main engine.
   */
  public void markAsCovered(CYieldPoint yp) {
    if(yp.getLocation() instanceof JoinPoint.StaticPart) {
      WeavePoint wp = new WeavePoint(yp.getSourceFile(), yp.getSourceLine(), yp.getSignature());
      Boolean b = _weavePoints.put(wp, true);
      if (b == null || b == false) {
        _coveredWeavePoints ++;
      }
    } 
  }
  
  /**
   * Handle an error.
   * @param e Exception object.
   */
  private void handleError(Throwable e) {
    // TODO Auto-generated method stub
  }

 
  /**
   * Dump weaver agent information.
   * @param out Output stream.
   */
  public void dumInformation(PrintStream out) {
    for (Entry<WeavePoint,Boolean> e : _weavePoints.entrySet()) {
      out.println((e.getValue() ? "*" : "-") + ' ' + e.getKey().toString());
    }
  }
  
  /**
   * Coverage log id. 
   */
  private static final String COVERAGE_REPORT_ID = "coverage";
  /**
   * Generate coverage report.
   * @throws IOException if an I/O error occurs.
   */
  public void produceCoverageReport() throws IOException {
     CReport r = CWorkspace.INSTANCE.createReport(COVERAGE_REPORT_ID);
     r.beginSection("GLOBAL STATISTICS", "TOTAL", "COVERED", "%");
     r.writeEntry(_weavePoints.size(),
                  _coveredWeavePoints,
                  getCoverageRate());
     r.beginSection("WEAVE POINTS", "C", "SOURCE FILE", "LINE", "SIGNATURE" );
     for (Entry<WeavePoint,Boolean> e : _weavePoints.entrySet()) {
       r.writeEntry(e.getValue() ? 'Y' : 'N',
                    e.getKey().getSourceFile(),
                    e.getKey().getSourceLine(),
                    e.getKey().getSignature());
     }
     r.close();
  }

  /**
   * Weave point information.
   * 
   * @since 0.2
   */
  public static class WeavePoint implements Comparable<WeavePoint> {
    /**
     * Source code file.
     */
    private final String _sourceFile;
    
    /**
     * Source code line.
     */
    private final int _sourceLine;
    
    /**
     * Signature.
     */
    private final String _signature;
    
    /**
     * Constructor.
     * @param sourceFile Source file.
     * @param sourceLine Source line.
     * @param signature Signature.
     */
    public WeavePoint(String sourceFile, int sourceLine, String signature) {
      _sourceFile = sourceFile;
      _sourceLine = sourceLine;
      _signature = signature;
    }

    /**
     * Get source file name.
     * @return The source file for the weave point.
     */
    public String getSourceFile() {
      return _sourceFile;
    }

    /**
     * Get source code line.
     * @return The source code line for the weave point.
     */
    public int getSourceLine() {
      return _sourceLine;
    }

    /**
     * Get signature.
     * @return The point-cut signature  for the weave point.
     */
    public String getSignature() {
      return _signature;
    }

    /**
     * Compare with another weave point.
     * Weave points are compared by source file first, then by source file, and finally by signature
     * @param other Another weave point.
     * @return Comparison result.
     */
    @Override
    public int compareTo(WeavePoint other) {
      int c = _sourceFile.compareTo(other._sourceFile);
      if (c == 0) {
        c = _sourceLine - other._sourceLine;
        if (c == 0) {
          c = _signature.compareTo(other._signature);
        }
      } 
      return c;
    }
    
    /**
     * Test for equality.
     * @param o Object reference.
     * @return <code>true</code> iff <code>o</code> refers to an equivalent weave point (same file, line, and signature).
     */
    @Override
    public boolean equals(Object o) {
      return o == this || (o instanceof WeavePoint && compareTo((WeavePoint) o) == 0);
    }
    
    /**
     * Get textual representation (used for debugging)
     * @return A string object
     */
    @Override
    public String toString() {
      return _sourceFile + ":" + _sourceLine + ":" + _signature;
    }
    
    /**
     * Get hash code.
     * @return Hash code for the object.
     */
    @Override
    public int hashCode() {
      return _sourceFile.hashCode() ^ _sourceLine ^ _signature.hashCode();
    }
  }

 


}
