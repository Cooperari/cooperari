package org.cooperari.junit;


import org.cooperari.CConfigurationError;
import org.cooperari.CInternalError;
import org.cooperari.core.CSession;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;

// TODO: document
// - fresh thread per trial test, test trial ends only when all spawned threads terminate
// - hotspots + CMaxTrials honored even in preemptive mode
// - timeout attribute not honored
// - setup + teardown + expected are honored
// - CIgnore annotation
/**
 * JUnit cooperative test runner.
 * 
 * <p>
 * JUnit test classes must be parameterized with this test runner for cooperative test execution.
 * The usual {@link org.junit.runner.RunWith} annotation should be used to set this runner for test classes,
 * as usual. Here is a simple example:
 * <pre>
 * import org.junit.runner.RunWith;
 * import org.cooperari.CJUnitRunner;
 * ...
 * &#064;RunWith(CJUnitRunner.class)
 * public class MyTestClass {
 *   ...
 * }
 * </p>
 * 
 * @see CIgnore
 * @since 0.2
 */
public final class CJUnitRunner extends BlockJUnit4ClassRunner {

  /**
   * Auxiliary object for interface with JUnit.
   */
  private static final RunnerScheduler JRUNNER_SCHEDULER =
      new RunnerScheduler() {

    @Override
    public void schedule(Runnable childStatement) {
      childStatement.run();
    }

    @Override
    public void finished() {

    }
  };


  /**
   * Constructs a new runner.
   * @param testClass Test class.
   * @throws InitializationError if an error occurs during initialization
   */
  public CJUnitRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
    super.setScheduler(JRUNNER_SCHEDULER);
  }

  /**
   * Callback method to execute a test method.
   * @param fm Method to run.
   * @param notifier Notifier instance.
   */
  @Override
  protected void runChild(FrameworkMethod fm, RunNotifier notifier) {
    Description desc = describeChild(fm);
    if (fm.getAnnotation(Ignore.class) != null) {
      // @Ignore annotation for method
      notifier.fireTestIgnored(desc); 
      return;
    }

    if (fm.getAnnotation(CIgnore.class) != null) {
      // Run test only once using the standard runner and preemptive semantics
      super.runChild(fm, notifier);
      return;
    }

    notifier.fireTestStarted(desc);

    if (fm.getAnnotation(Test.class).timeout() > 0) {
      // timeout attribute not honored for now
      notifier.fireTestFailure(new Failure(desc, new CConfigurationError("'timeout' attribute is not honored for @Test. Sorry :(")));
      return;
    }

    final Class<?> expectedException = fm.getAnnotation(Test.class).expected();
    final Statement statement = createJUnitStatement(fm, false);

    Runnable runMethod = new MethodRunner(fm, statement); 
    CSession.ExecutionHandler executionHandler = new CSession.ExecutionHandler() {
      @Override
      public void onNormalCompletion() throws Throwable {
        if (expectedException != Test.None.class) {
          throw new AssertionError("Expected exception " + expectedException.getCanonicalName());
        }
      }
      @Override
      public boolean ignoreException(Throwable e)  {
        return e.getClass() == expectedException;
      }
    };

    try {
      CSession.execute(fm.getMethod(), runMethod, executionHandler);
      notifier.fireTestFinished(desc);
    } catch (Throwable e) {
      notifier.fireTestFailure(new Failure(desc, e));
    }
  }

  /**
   * Create JUnit statement to run a test.
   * @param fm Method.
   * @param handleExpected If <code>true</code>, let JUnit handle {@link Test#expected()}.
   * @return JUnit statement object
   */
  private Statement createJUnitStatement(FrameworkMethod fm, boolean handleExpected) {
    try {
      Object test = super.createTest();
      Statement statement = super.methodInvoker(fm, test);
      if (handleExpected) {
        statement = super.possiblyExpectingExceptions(fm, test, statement);
      }
      statement = super.withBefores(fm, test, statement);
      statement = super.withAfters(fm, test, statement);
      return statement;
    } catch(Throwable e) {
      throw new CInternalError(e);
    }
  }
  
  /**
   * Method runner thread.
   * @since 0.2 
   */
  public class MethodRunner extends Thread {
    /** 
     * JUnit statement 
     */
    private final Statement _statement;

    /**
     * Constructs a new method runner.
     * @param fm Method handle.
     * @param statement JUnit statement to run the method.
     */
    public MethodRunner(FrameworkMethod fm, Statement statement) {
      setName(fm.getName());
      _statement = statement;
    }

    /** 
     * Execution method. 
     */
    @Override
    public void run () {
      try {
        _statement.evaluate();
      } catch(RuntimeException|Error e) {
        throw e;
      } catch(Throwable e) {
        throw new CInternalError(e);
      }
    }
  }

 
}