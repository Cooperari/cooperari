package org.cooperari.feature.data;


import java.util.HashMap;

import org.cooperari.config.CRaceDetection;
import org.cooperari.core.CRuntime;
import org.cooperari.core.CThread;
import org.cooperari.core.CTrace;
import org.cooperari.core.CWorkspace;
import org.cooperari.errors.CInternalError;
import org.cooperari.errors.CRaceError;


/**
 * Race detector.
 *
 * @since 0.2
 */
public class RaceDetector {
  /**
   * Internal state.
   */
  private final HashMap<Data, Status> _monitoring = new HashMap<>();

  /**
   * Exception throwing flag.
   */
  private final boolean _throwRaceErrors;

  /**
   * Constructor.
   * @param config Configuration.
   */
  public RaceDetector(CRaceDetection config) {
    _throwRaceErrors = config.throwErrors(); 
    assert CWorkspace.debug("RD initialized | throwErrors=%s", _throwRaceErrors);
  }

  /**
   * Signal beginning of read access.
   * @param o Object.
   * @param key Data key.
   */
  public void beginRead(Object o, Object key) {
    beginRead(new Data(o, key));
  }

  /**
   * Signal end of read access.
   * @param o Object.
   * @param key Data key.
   */
  public void endRead(Object o, Object key) {
    endRead(new Data(o, key));
  }

  /**
   * Signal beginning of write access.
   * @param o Object.
   * @param key Data key.
   */
  public void beginWrite(Object o, Object key) {
    beginWrite(new Data(o,key));
  }

  /**
   * Signal end of write access to object field.
   * @param o Object.
   * @param key Data key.
   */
  public void endWrite(Object o, Object key) {
    endWrite(new Data(o,key));
  }



  @SuppressWarnings("javadoc")
  private void beginRead(Data d) {
    Status status = _monitoring.get(d);
    if (status == null) {
      status = new Status();
      _monitoring.put(d, status);
    } 
    status.incrementReaders();
    assert CWorkspace.debug("begin read | %s %s", d, status);
  }

  @SuppressWarnings("javadoc")
  private void endRead(Data d) {
    Status status = _monitoring.get(d);
    if (status == null || status.readers() == 0) {
      throw new CInternalError();
    }

    if (status.decrementReaders() == 0) {
      _monitoring.remove(d);
    } 
    assert CWorkspace.debug("end read | %s %s", d, status);

    if (status.raceCondition()){
      reportRace(d);
    }
  }

 
  @SuppressWarnings("javadoc")
  private void beginWrite(Data d) {
    Status status = _monitoring.get(d);
    if (status == null) {
      status = new Status();
      _monitoring.put(d, status);
    }
    status.incrementWriters();
    assert CWorkspace.debug("begin write | %s %s", d, status);

  }

  @SuppressWarnings("javadoc")
  private void endWrite(Data d) {
    Status status = _monitoring.get(d);
    if (status == null || status.writers() == 0) {
      throw new CInternalError();
    }
    if (status.decrementWriters() == 0) {
      _monitoring.remove(d);
    } 
    assert CWorkspace.debug("end write | %s %s", d, status);
    if (status.raceCondition()){
      reportRace(d);
    }
  }

  @SuppressWarnings("javadoc")
  private void reportRace(Data d) {
    CThread t = (CThread) Thread.currentThread();
    CTrace trace = CRuntime.getRuntime().get(CTrace.class);
    trace.record(t, CTrace.EventType.RACE);
    if (_throwRaceErrors) {
      String msg = 
          String.format
          ("Race: %s at %s:%d over %s.%s", 
              t.getName(), 
              t.getLocation().getYieldPoint().getSourceFile(),
              t.getLocation().getYieldPoint().getSourceLine(),
              d._object.getClass().getCanonicalName(), 
              d._key);
      throw new CRaceError(msg);
    }
  }

  /**
   * Field representation, and (object, field name) name pair.
   */
  private static class Data {
    /**
     * Object.
     */
    private Object _object;
    /**
     * Key (field name or array index).
     */
    private Object _key;
    /**
     * Cached hash code.
     */
    private int _hash;
    /**
     * Constructor.
     * @param object Object.
     * @param key Data access key.
     */
    public Data(Object object, Object key) {
      _object = object;
      _key = key;
      _hash = System.identityHashCode(_object) ^ key.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("javadoc")
    public String toString() {
      return new StringBuilder()
      .append(_object.getClass())
      .append('(')
      .append(_hash)
      .append(')')
      .append(',')
      .append(_key)
      .toString();

    }
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("javadoc")
    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Data))
        return false;

      Data d = (Data) o;

      return  _object == d._object && _key.equals(d._key);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @SuppressWarnings("javadoc")
    @Override
    public int hashCode() {
      return _hash;
    }
  }
  /**
   * Status information per field.
   */
  private static class Status {
    /**
     * Number of readers.
     */
    private int _readers;
    /**
     * Number of writers.
     */
    private int _writers;
    /**
     * Race flag.
     */
    private boolean _race;

    /**
     * Constructor.
     */
    public Status() {
      _readers = 0;
      _writers = 0;
      _race = false;
    }
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("javadoc")
    @Override
    public String toString() {
      return new StringBuilder()
      .append("[")
      .append(_race)
      .append(',')
      .append(_readers)
      .append(',')
      .append(_writers)
      .append(']')
      .toString();
    }
    /**
     * Increment number of writers.
     */
    public void incrementWriters() {
      if (_writers != 0 || _readers != 0) {
        _race = true;
      }
      _writers++;
    }

    /**
     * Increment number of writers.
     */
    public void incrementReaders() {
      _readers++;
      if (_writers > 0) {
        _race = true;
      }
    }

    /**
     * Decrement number of readers.
     * @return Updated reference count (readers + writers).
     */
    public int decrementReaders() {
      _readers --;
      return _readers + _writers;
    }

    /**
     * Decrement number of readers.
     * @return Updated reference count (readers + writers).
     */
    public int decrementWriters() {
      _writers --;
      return _readers + _writers;
    }

    /**
     * Get number of readers.
     * @return Reader count.
     */
    public int readers() {
      return _readers;
    }

    /**
     * Get number of writers.
     * @return Writer count.
     */
    public int writers() {
      return _writers;
    }

    /**
     * Check if race condition occurred.
     * @return Race condition flag.
     */
    public boolean raceCondition() {
      return _race;
    }

  }
}
