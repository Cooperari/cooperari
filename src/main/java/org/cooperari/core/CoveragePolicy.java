package org.cooperari.core;
import java.util.List;

public interface CoveragePolicy {
  
  CThread decision(List<CThread> readyThreads);
  boolean done();
  
  void onTestStarted();
  void onTestFailure();
  void onTestFinished();  
}