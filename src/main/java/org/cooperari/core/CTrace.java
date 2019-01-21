package org.cooperari.core;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.cooperari.config.CTraceOptions;
import org.cooperari.core.scheduling.CThreadLocation;
import org.cooperari.core.scheduling.CYieldPoint;
import org.cooperari.core.util.CReport;

/**
 * Cooperative execution trace.
 * 
 * @since 0.2
 *
 */
public final class CTrace {
  
  /**
   * Event types.
   *
   */
  public enum EventType {
    /**
     * Race condition.
     */
    RACE('R'),
    /**
     * Deadlock
     */
    DEADLOCK('D');
    
    /**
     * Event marker in trace file.
     */
    private final char _marker;
    
    /**
     * Constructor.
     * @param marker Event marker for trace file.
     */
    private EventType(char marker) {
      _marker = marker;  
    }
    
    /**
     * Get event marker character.
     * @return Character mark in trace files for this event type.
     */
    public char getTraceMarker() {
      return _marker;
    }
  };

  /**
   * Size limit.
   */
  private final int _sizeLimit;

  /**
   * Trace steps.
   */
  private final ArrayDeque<TraceItem> _traceElements = new ArrayDeque<>();

  /**
   * Map of thread identifiers to names.
   */
  private final HashMap<Integer,ThreadInfo> _threadNames = new HashMap<>();
  

  /**
   * Coverage log to use.
   */
  private final CCoverageLog _clog;

  /**
   * Constructs a new trace.
   * @param clog Set in which to record covered yield points.
   * @param options Options.
   */
  public CTrace(CCoverageLog clog, CTraceOptions options) {
    _clog = clog;
    _sizeLimit = options.limit();
  }


  /**
   * Record existence of thread. 
   * Implementation note: not done every time through {@link #recordStep} for efficiency reasons.
   * @param t The thread at stake.
   */
  public void recordThread(CThread t) {
    _threadNames.put(t.getCID(), new ThreadInfo(t));
  }

  /**
   * Record a step for one thread.
   * @param t The thread at stake.
   */
  public void recordStep(CThread t) {
    record(t, null);
  }
  
  /**
   * Record an event for one thread.
   * @param t The thread at stake.
   * @param type event type
   */
  public void record(CThread t, EventType type) {
    CYieldPoint yp = t.getLocation().getYieldPoint();
    if (yp.getSourceFile() != CYieldPointImpl.INTERNAL) {
      _clog.markAsCovered(yp);
    }
    _traceElements.addLast(new TraceItem(t, type));
    if (_sizeLimit > 0 && _traceElements.size() == _sizeLimit) {
      _traceElements.removeFirst();
    }
  }

  /**
   * Write trace to a output file.
   * @param report Output file.
   * @param failure Optional failure that may have happened during execution.
   * @throws IOException If an I/O error occurs.
   */
  public void save(CReport report, Optional<Throwable> failure) throws IOException {
    // Write thread info
    report.beginSection("THREADS", "TID", "NAME", "CLASS");
    for (Map.Entry<Integer, ThreadInfo> entry : _threadNames.entrySet()) {
      ThreadInfo ti = entry.getValue();
      report.writeEntry(entry.getKey(), ti.getName(), ti.getClassName());
    }
    // Write step info
    int stepId = 0;  
    report.beginSection("EXECUTION TRACE", 
                        "#", 
                        "TID", 
                        "STEP", 
                        "EVENT",
                        "SOURCE FILE", 
                        "LINE", 
                        "YIELD POINT", 
                        "STAGE"); 
    for (TraceItem traceItem : _traceElements) {
      CThreadLocation location = traceItem.getLocation();
      CYieldPoint yp = location.getYieldPoint();
      report.writeEntry(stepId, 
                        traceItem.getThreadId(), 
                        traceItem.getThreadStep(), 
                        traceItem.getEventMarker(),
                        yp.getSourceFile(), 
                        yp.getSourceLine(), 
                        yp.getSignature(), 
                        location.getStage());
      stepId++;
    }
    
    if (failure.isPresent()) {
      report.beginSection("STACK TRACE FOR FAILURE");
      report.dumpStackTrace(failure.get());
    }
  }
  
  /**
   * Reset.
   */
  public void reset() {
    _traceElements.clear();
    _threadNames.clear();
  }

  /**
   * Inner class representing thread information.
   */
  private static final class ThreadInfo {
    /**
     * Thread name.
     */
    private String _threadName;

    /**
     * Thread class name.
     */
    private String _className;

    /**
     * Constructs information for a thread.
     * @param t Thread at stake.
     */
    ThreadInfo(CThread t) {
      _threadName = t.getName();
      _className = t.getRunnable().getClass().getName();
    }

    /**
     * Get name for thread.
     * @return Thread name.
     */
    public String getName() {
      return _threadName;
    }

    /**
     * Get class name.
     * @return Class name for thread.
     */
    public String getClassName() {
      return _className;
    }
  }
  /**
   * Inner class representing an item of a trace.
   */
  private static final class TraceItem {
    /**
     * Thread id.
     */
    private final int _threadId;

    /**
     * Thread step.
     */
    private final int _threadStep;

    /**
     * Type.
     */
    private final char _eventMarker;
    
    /**
     * Yield point for thread.
     */
    private final CThreadLocation _location;

    /**
     * Constructs a new trace element.
     * @param t Thread.
     * @param type Type of element.
     */
    TraceItem(CThread t, EventType type)  {
      this._threadId = t.getCID();
      this._threadStep = t.getStep();
      this._eventMarker = type != null ? type.getTraceMarker() : '-';
      this._location = t.getLocation();
    }
    
    /**
     * Get thread id.
     * @return The thread id for this step.
     */
    int getThreadId() {
      return _threadId;
    }

    /**
     * Get thread step.
     * @return The thread step counter for this step.
     */
    int getThreadStep() {
      return _threadStep;
    }
    
    /**
     * Get event type.
     * @return Type of event.
     */
    char getEventMarker() {
      return _eventMarker;
    }
    /**
     * Get yield point.
     * @return The thread step counter for this step.
     */
    CThreadLocation getLocation() {
      return _location;
    }
  }
}
