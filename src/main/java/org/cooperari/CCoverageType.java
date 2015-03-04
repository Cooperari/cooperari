package org.cooperari;

import org.cooperari.core.CoveragePolicy;
import org.cooperari.coverage.HistoryDependentCoverage;
import org.cooperari.coverage.RandomCoverage;

public enum CCoverageType {
  NONE(null), NOISE(null), RANDOM(RandomCoverage.class), HDC(
      HistoryDependentCoverage.class);

  public static final CCoverageType DEFAULT = HDC;

  private Class<? extends CoveragePolicy> _clazz;

  private CCoverageType(Class<? extends CoveragePolicy> clazz) {
    _clazz = clazz;

  }

  public Class<? extends CoveragePolicy> getImplementation() {
    return _clazz;
  }
}