package org.cooperari.feature.threadrunner;

import static org.cooperari.core.CThreadState.CBLOCKED;
import static org.cooperari.core.CThreadState.CREADY;

import java.util.Iterator;
import java.util.LinkedList;

import org.cooperari.core.CEngine;
import org.cooperari.core.COperation;
import org.cooperari.core.CRuntime;
import org.cooperari.core.CThread;
import org.cooperari.core.CThreadState;
import org.cooperari.core.CUncaughtExceptionHandler;

/**
 * Thread runner operation.
 * 
 * @since 0.2
 *
 */
final class ThreadRunnerOperation extends COperation<Void> {

  /**
   * Execute a thread runner operation.
   * @param thisThread Current thread.
   * @param runnables Runnable instances.
   * @throws IllegalThreadStateException if any of the runnable instances is a
   *         actually an alive {@link Thread} object.
   * @see Thread#isAlive()
   */
  public static void execute(CThread thisThread, Runnable[] runnables) {
    thisThread.cYield(CThread.NOP);
    for (Runnable r : runnables) {
      if (r instanceof Thread) {
        Thread t = (Thread) r;
        if (t.isAlive()) {
          String msg = String.format(
              "Trying to start alive thread '%s' of type '%s'.", t.getName(),
              t.getClass());
          throw new IllegalThreadStateException(msg);
        }
      }
    }
    thisThread.cYield(new ThreadRunnerOperation(runnables));
  }
  /**
   * Uncaught exception handler.
   */
  private final CUncaughtExceptionHandler _excHandler = new CUncaughtExceptionHandler();

  /**
   * Threads created by the operation.
   */
  private final LinkedList<CThread> _threadList = new LinkedList<>();

  /**
   * Create service.
   * @param runnables Array of {@link Runnable} instances for the threads for execute.
   */
  private ThreadRunnerOperation(Runnable[] runnables) {
    CRuntime rt = CRuntime.getRuntime();
    CEngine s = rt.get(CEngine.class);
    for (int i = 0; i < runnables.length; i++) {
      _threadList.add(s.createNewThread(runnables[i], _excHandler));
    }
  }
  
  /**
   * Get yield point stage.
   * @return <code>1</code>
   */
  @Override
  public int getStage() {
    return 1;
  }

  /**
   * Execution method.
   */
  @Override
  public CThreadState getState() {
    if (_threadList.isEmpty()) {
      return CREADY;
    }
    Iterator<CThread> itr = _threadList.iterator();
    while (itr.hasNext() && itr.next().getState() == Thread.State.TERMINATED) {
      itr.remove();
    }
    return _threadList.isEmpty() ?  CREADY : CBLOCKED;
  }

  /**
   * Complete the operation. 
   * This results in a call to {@link CUncaughtExceptionHandler#rethrowExceptionsIfAny() rethrowExceptionsIfAny()}
   * for the internal {@link CUncaughtExceptionHandler} object.
   * @see CUncaughtExceptionHandler#rethrowExceptionsIfAny()
   */
  @Override
  public void execute() {
    _excHandler.rethrowExceptionsIfAny();
  }
}
