package org.cooperari.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.cooperari.CTraceOptions;
import org.cooperari.core.aspectj.AgentFacade;
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
   * Map of thread ids to names.
   */
  private final HashMap<Integer,ThreadInfo> _threadNames = new HashMap<>();

  /**
   * Step counter.
   */
  private int _stepCounter = 0;

  /**
   * Constructs a new trace.
   * @param options Options.
   */
  public CTrace(CTraceOptions options) {
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
    AgentFacade.INSTANCE.markAsCovered(t.getYieldPoint());
    record(t, null);
  }
  
  /**
   * Record an event for one thread.
   * @param t The thread at stake.
   * @param type event type
   */
  public void record(CThread t, EventType type) {
    _traceElements.addLast(new TraceItem(t, type));
    if (_sizeLimit > 0 && _traceElements.size() == _sizeLimit) {
      _traceElements.removeFirst();
    }
    _stepCounter++;
  }

  /**
   * Write trace to file.
   * @param file Output file.
   * @throws IOException If an I/O error occurs.
   */
  public void write(File file) throws IOException {
    PrintStream out = new PrintStream(file);
    print(new PrintStream(file) );
    out.close();
  }

  /**
   * Write trace to an output stream.
   * @param out Output stream.
   */
  public void print(PrintStream out) {
    // Write thread info
    CReport report = new CReport(out);
    report.beginSection("THREADS", "TID", "NAME", "CLASS");
    for (Map.Entry<Integer, ThreadInfo> entry : _threadNames.entrySet()) {
      ThreadInfo ti = entry.getValue();
      report.writeEntry(entry.getKey(), ti.getName(), ti.getClassName());
    }
    // Write step info
    int stepId = _stepCounter - _traceElements.size();  
    report.beginSection("STEPS", 
                        "#", 
                        "TID", 
                        "STEP", 
                        "EVENT",
                        "SOURCE FILE", 
                        "LINE", 
                        "YIELD POINT", 
                        "STAGE"); 
    
    for (TraceItem traceItem : _traceElements) {
      CYieldPoint yp = traceItem.getYieldPoint();
      report.writeEntry(stepId, 
                        traceItem.getThreadId(), 
                        traceItem.getThreadStep(), 
                        traceItem.getEventMarker(),
                        yp.getSourceFile(), 
                        yp.getSourceLine(), 
                        yp.getSignature(), 
                        yp.getStage());
      stepId++;
    }
  }
  
  /**
   * Clear the trace.
   */
  public void clear() {
    _traceElements.clear();
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
    private final CYieldPoint _yieldPoint;

    /**
     * Constructs a new trace element.
     * @param t Thread.
     * @param type Type of element.
     */
    TraceItem(CThread t, EventType type)  {
      this._threadId = t.getCID();
      this._threadStep = t.getStep();
      this._eventMarker = type != null ? type.getTraceMarker() : '-';
      this._yieldPoint = t.getYieldPoint();
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
    CYieldPoint getYieldPoint() {
      return _yieldPoint;
    }
  }
}
