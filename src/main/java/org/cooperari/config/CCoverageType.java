package org.cooperari.config;

import org.cooperari.core.coverage.HistoryDependentCoverage;
import org.cooperari.core.coverage.RandomCoverage;
import org.cooperari.scheduling.CScheduler;

public enum CCoverageType {
  NONE(null), NOISE(null), RANDOM(RandomCoverage.class), HDC(
      HistoryDependentCoverage.class);

  public static final CCoverageType DEFAULT = HDC;

  private Class<? extends CScheduler> _clazz;

  private CCoverageType(Class<? extends CScheduler> clazz) {
    _clazz = clazz;

  }

  public Class<? extends CScheduler> getImplementation() {
    return _clazz;
  }
}