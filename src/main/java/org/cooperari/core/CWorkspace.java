package org.cooperari.core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

import org.aspectj.lang.JoinPoint;
import org.cooperari.CConfigurationError;
import org.cooperari.core.util.CLog;
import org.cooperari.core.util.CReport;


/**
 * Keeps a record of the Cooperari workspace directory and
 * global configurations, and provides associated utility methods.
 * 
 * Note that only a singleton instance of this type exists ({@link #INSTANCE}).
 * 
 * 
 * @since 0.2
 */
public enum CWorkspace {
  /**
   * Singleton instance.
   */
  INSTANCE;

  /**
   * Workspace options.
   * 
   * @since 0.2
   */
  public enum Option {
    /**
     * Log output to <code>System.out</code>
     */
    LOG_TO_STDOUT
  }
  /**
   * Root directory for workspace.
   */
  private File _root; 

  /**
   * Main log id.
   */
  private static final String MAIN_LOG_ID = "cooperari";
  
  /**
   * Debug log id.
   */
  private static final String DEBUG_LOG_ID = "debug";

  /**
   * Log suffix.
   */
  private static final String LOG_SUFFIX = ".log.txt";

  /**
   * Report suffix.
   */
  private static final String REPORT_SUFFIX = ".report.txt";

  /**
   * Main log. 
   */
  private CLog _mainLog;
  
  /**
   * Main log. 
   */
  private CLog _debugLog;

  /**
   * Constructor. 
   */
  private CWorkspace() {
    _root = null; // not initialized; initialize() must be called
  }

  /**
   * Check if workspace is initialized.
   * @return <code>true</code> if the workspace has been initialized before.
   * @see #initialize(File)
   */
  public boolean isInitialized() {
    return _root != null; 
  }

  /**
   * Initialize the workspace using supplied directory as root.
   * 
   * This method should be called once to initialize the workspace.
   * 
   * @param root Root directory.
   * @param options Workspace options.
   * @throws IOException If an I/O error occurs.
   * @throws CConfigurationError If the workspace has already been initialized or some other error occurs.
   */
  public void initialize(File root, Option... options) throws IOException, CConfigurationError {
    if (isInitialized()) {
      throw new CConfigurationError("Workspace already initialized.");
    }
    if (root.isFile()) {
      throw new CConfigurationError("Cannot use normal file as workspace directory: "+ root.getAbsolutePath());
    }
    root.mkdirs();
    boolean assertionsEnabled = false;
    assert assertionsEnabled = true;
    if (assertionsEnabled) {
      _debugLog = new CLog(new File(root, DEBUG_LOG_ID + LOG_SUFFIX), CLog.Option.WALLTIME_TIMESTAMPS);
    }
    EnumSet<Option> optionSet = 
      (options != null && options.length > 0) ?
         EnumSet.copyOf(Arrays.asList(options)) :
         EnumSet.noneOf(Option.class);     
    CLog parentLog = null;
    if (optionSet.contains(Option.LOG_TO_STDOUT)) {
      parentLog = CLog.SYSTEM_OUT;
    } 
    _mainLog = new CLog(new File(root, MAIN_LOG_ID + LOG_SUFFIX), parentLog, CLog.Option.OMMIT_THREAD_INFO);
    _root = root;
  }

  /**
   * Get root directory.
   * @return The root directory of the workspace.
   * @throws CConfigurationError If the workspace has not been initialized.
   */
  public File getRootDirectory() {
    checkForInitialization();
    return _root;
  }


 

  /**
   * Create a file in the workspace directory.
   * 
   * <p>
   * If the file does not exist already, it is created according to the relative path supplied as argument.
   * All nonexistent parent directories are also created if necessary.
   * </p> 
   * 
   * @param relativePath File object with a relative path set.
   * @return The file object.
   * @throws CConfigurationError If the workspace has not been initialized.
   * @throws IOException If an I/O error occurs creating the file.
   */
  public File createFile(String relativePath) throws IOException {
    checkForInitialization();
    File f = new File(_root, relativePath);
    f.getParentFile().mkdirs();
    f.createNewFile();
    return f;
  }

  /**
   * Create a log. 
   * @param logName Name for the log.
   * @param options Log options.
   * @return A {@link CLog} instance.
   * @throws IOException If an I/O error occurs.
   * @throws CConfigurationError if the workspace is not initialized. 
   */
  public CLog createLog(String logName, CLog.Option... options) throws CConfigurationError, IOException {
    checkForInitialization();
    return new CLog(createFile(logName.concat(LOG_SUFFIX)), options); 
  }

  /**
   * Create a report. 
   * @param reportName Name for the report.
   * @return A {@link CReport} instance.
   * @throws IOException If an I/O error occurs.
   * @throws CConfigurationError if the workspace is not initialized. 
   */
  public CReport createReport(String reportName) throws CConfigurationError, IOException {
    checkForInitialization();
    return new CReport(createFile(reportName.concat(REPORT_SUFFIX))); 
  }


  @SuppressWarnings("javadoc")
  private void checkForInitialization() {
    if (!isInitialized()) {
      throw new CConfigurationError("Workspace has not been initialized.");
    }
  }

  /**
   * Get workspace log.
   * @return The main log instance. 
   */
  public static CLog getLog() {
    INSTANCE.checkForInitialization();
    return INSTANCE._mainLog;
  }
  
  /**
   * Log exception to the workspace log.
   * @param e Exception
   */
  public static void log(Throwable e) {
    getLog().message(e);
  }

  /**
   * Log message to the workspace log.
   * @param format Print format.
   * @param args Arguments for print format.
   */
  public static void log(String format, Object... args) {
    INSTANCE.checkForInitialization();
    getLog().message(format, args);
  }

  /**
   * Get workspace log.
   * @return The main log instance. 
   */
  public static CLog getDebugLog() {
    INSTANCE.checkForInitialization();
    return INSTANCE._debugLog;
  }
  
  /**
   * Log a thread exception to the debug log.
   * @param t Thread.
   * @param e Exception.
   * @return <code>true</code> for a convenient combination with <code>assert</code> statements.
   */
  public static boolean debug(Thread t, Throwable e) {
    CLog log = getDebugLog();
    if (log!= null) {
      log.message("Thread '%s' throwed up '%s'", t.getName(), e.getClass());
      log.message(e);
    }
    return true;
  }

  /**
   * Log a join point to the debug log.
   * @param jp Join point.
   * @return <code>true</code> for a convenient combination with <code>assert</code> statements.
   */
  public static boolean debug(JoinPoint jp) {
    return debug("Join point: %s", jp.toString());
  }

  /**
   * Log a message to the debug log.
   * @param format Format.
   * @param args Arguments.
   * @return <code>true</code> for a convenient combination with <code>assert</code> statements.
   */
  public static boolean debug(String format, Object... args) {
    CLog log = getDebugLog();
    if (log != null) {
      log.message(format, args);
    }
    return true;
  }

 
}
