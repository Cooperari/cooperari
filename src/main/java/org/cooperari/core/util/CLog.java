package org.cooperari.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;

import org.cooperari.errors.CInternalError;

/**
 * Message log.
 * 
 * @since 0.2
 */
public final class CLog {

  /**
   * Logging options.
   * 
   * @since 0.2
   *
   */
  public enum Option { 
    /**
     * Write plain messages to a log, without any other information (date, or thread info for example).
     * (implies {@link #OMMIT_THREAD_INFO}).
     */
    PLAIN_FORMAT,

    /**
     * Omit thread info.  
     */
    OMMIT_THREAD_INFO,

    /**
     * Log time info using wall-time rather than logging the date and plain time of messages.
     */
    WALLTIME_TIMESTAMPS;
  }
  /**
   * Predefined log instance uses {@link System#out} for output.
   */
  public static final CLog SYSTEM_OUT = new CLog(System.out, Option.PLAIN_FORMAT);

  /**
   * Predefined log instance that uses {@link System#err} for output.
   */
  public static final CLog SYSTEM_ERR = new CLog(System.out, Option.PLAIN_FORMAT);

  /**
   * Print lock to enable non-garbled output.
   */
  private final Object _lock = new Object();

  /**
   * Output stream.
   */
  private final PrintStream _out;

  /**
   * Parent log.
   */
  private final CLog _parent;

  /**
   * Creation time.
   */
  private final long _creationTime;

  /**
   * Plain format flag.
   */
  private boolean _plainFormat = false;

  /**
   * Thread info flag.
   */
  private boolean _noThreadInfo = false;

  /**
   * Wall-time flag.
   */
  private boolean _useWallTime = false;

  /**
   * Constructs a new log using the supplied print stream for output.
   * 
   * @see #CLog(PrintStream, CLog, Option...)
   * @param out Output stream.
   * @param options Log options.
   */
  public CLog(PrintStream out, Option... options) {
    this(out, null, options);
  }

  /**
   * Constructs a new log redirecting output to given file.
   * 
   * @see #CLog(PrintStream, CLog, Option...)
   * @param outputFile Output file.
   * @param options Log options.
   * @throws FileNotFoundException If the output file cannot be created or
   *         written to.
   */
  public CLog(File outputFile, Option... options) throws FileNotFoundException {
    this(new PrintStream(outputFile), null, options);
  }

  /**
   * Constructs a new log redirecting output to given file and with an
   * associated parent log.
   * 
   * @see #CLog(PrintStream, CLog, Option...)
   * @param outputFile Output file.
   * @param parent Parent log.
   * @param options Log options.
   * @throws FileNotFoundException If the output file cannot be created or
   *         written to.
   */
  public CLog(File outputFile, CLog parent, Option... options) throws FileNotFoundException {
    this(new PrintStream(outputFile), parent, options);
  }


  /**
   * Constructs a new log using the supplied print stream for output and
   * an already defined log as parent.
   * <p>
   * The parent log stream will get the same messages as this log stream, in addition to its own. 
   * For instance, it may be convenient to use {@link #SYSTEM_OUT} or
   * {@link #SYSTEM_ERR} as the parent log, so that messages can be
   * logged both to an application-defined stream and to one of the standard
   * output streams.
   * </p>
   * 
   * @param out Output stream.
   * @param parent Parent log.
   * @param options Log options.
   */
  public CLog(PrintStream out, CLog parent, Option... options) {
    _out = out;
    _parent = parent;
    _creationTime = System.currentTimeMillis();
    if (options != null && options.length > 0) {
      for (Option o : options) {
        switch(o) {
          case PLAIN_FORMAT:
            _plainFormat = true;
            break;
          case OMMIT_THREAD_INFO:
            _noThreadInfo = true;
            break;
          case WALLTIME_TIMESTAMPS:
            _useWallTime = true;
          default:
            break;
        }
      }
    }
  }


  /**
   * Log a message.
   * 
   * @param format Print format.
   * @param args Arguments for print format.
   */
  public void message(String format, Object... args) {
    write(format, args);
  }

  /**
   * Log an error or exception
   * 
   * @param e Throwable object.
   */
  public void message(Throwable e) {
    synchronized (_lock) {
      e.printStackTrace(_out);
    }
  }

  /**
   * Close the log. The parent log, if one is defined, is not closed.
   */
  public void close() {
    _out.close();
  }

  /**
   * Write a message to the log.
   * 
   * @param format Print format.
   * @param args Print arguments.
   */
  private void write(String format, Object... args) {
    if (_parent != null) {
      _parent.write(format, args);
    }
    synchronized (_lock) {
      try {
        if (! _plainFormat) {
          long time =  System.currentTimeMillis();
          if (_useWallTime) {
            long wallTime = time - _creationTime;
            _out.printf("%09d", wallTime);
          } else {
            _out.printf("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", new Date(time));
          }
          _out.print(" | ");
          if (! _noThreadInfo) {
            _out.print(Thread.currentThread().getName());
            _out.print(" | ");
          }
        }
        _out.printf(format, args);
        _out.println();
        _out.flush();
      } catch (Throwable e) {
        throw new CInternalError(e);
      }
    }
  }
}
