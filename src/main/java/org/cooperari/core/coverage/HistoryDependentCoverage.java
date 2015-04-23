package org.cooperari.core.coverage;


import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.cooperari.core.CRuntime;
import org.cooperari.core.CThread;
import org.cooperari.core.CWorkspace;
import org.cooperari.scheduling.CScheduler;

/**
 * Synchronization coverage with a few twists.
 *
 */
public class HistoryDependentCoverage implements CScheduler {

  private static final boolean PREEMPTION_BOUND_HEURISTIC;
  
  static { 
    PREEMPTION_BOUND_HEURISTIC = System.getProperty("cooperari.pbh") != null;
  }

  /**
   * History.
   */
  private HashSet<HistoryElement> _history;

  
  /**
   * Number of runs. 
   */
  private int _runs;

  /**
   * History size book-keeping (before running the test each time).
   */
  private int _prevHistorySize;
  
  /**
   * Pseudo-random number generator.
   */
  private Random _pRNG;

  /**
   * Last choice
   */
  private CThread _lastChoice;
  
  /**
   * Constructor.
   * @param testClass Test class.
   * @param testMethod Method.
   */
  public HistoryDependentCoverage(CRuntime runtime) {
    _pRNG = new Random(0); // IMPORTANT: use a fixed seed for repeatable tests
    _history = new HashSet<>();
    _runs = 0;
  }

  /*
   * (non-Javadoc)
   * @see org.cooperari.core.CoveragePolicy#onTestStarted()
   */
  @Override
  public void onTestStarted() {
    _prevHistorySize = _history.size();
    _lastChoice = null;
  }
  
  /*
   * (non-Javadoc)
   * @see org.cooperari.core.CoveragePolicy#onTestFailure()
   */
  @Override
  public void onTestFailure() {
    assert CWorkspace.debug("run: %d history: %d -> %d", _runs, _prevHistorySize, _history.size());
  }

  /*
   * (non-Javadoc)
   * @see org.cooperari.core.CoveragePolicy#onTestFinished()
   */
  @Override
  public void onTestFinished() {
    _runs++;
  }

  /*
   * (non-Javadoc)
   * @see org.cooperari.core.CoveragePolicy#done()
   */
  @Override
  public boolean done() {
    assert CWorkspace.debug("run: %d history: %d -> %d", _runs, _prevHistorySize, _history.size());
    return  _prevHistorySize == _history.size();
  }

  @Override
  public CThread decision(List<CThread> readyThreads) {
    CThread t = null;
       ProgramStateAbstraction ps = new ProgramStateAbstraction(readyThreads);
   
    assert ps.dumpDebugInfo();
    
    HistoryElement he = null;

    t = _lastChoice;
    if (PREEMPTION_BOUND_HEURISTIC && t != null && t.isReady()) {
      // Try to minimize preemptions.
      he = new HistoryElement(t, ps);
      if (_history.add(he)) {
        return t;
      }
    }
    // Pick a random thread index to start with.
    int tries = 0;
    int n = readyThreads.size();
    int index = _pRNG.nextInt(n);  
    // Now loop until an unexplored option appears.
    do {
      t = readyThreads.get(index);
      he = new HistoryElement(t, ps);
      tries++;
      index = (index+1) % n;
    } while (!_history.add(he) && tries < n);
    _lastChoice = t;
    assert CWorkspace.debug("history size: %d\n", _history.size());
    return t;
  }

  
}