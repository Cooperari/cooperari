package org.cooperari;

/**
 * Interface for providers of basic test coverage information.
 *
 * @since 0.2
 */
public interface CoverageInfo {
 
  /**
   * Get number of yield points covered.
   * @return Number of yield points covered by test trials.
   */
  int getCoveredYieldPoints();

  /**
   * Get total yield points.
   * <p>
   * This figure indicates the total number of yield points that were not covered
   * for the same set of source files where at least one yield point was covered.
   * </p>
   * @return Number of yield points covered by all test trials.
   */
  int getTotalYieldPoints();

  /**
   * Get coverage rate.
   * 
   * <p>
   * The default method takes the {@link #getCoveredYieldPoints()} / {@link #getTotalYieldPoints()}
   * ratio. It should not be redefined unless there is a (very) good reason to do so.
   * </p>
   * @return A value between <code>0</code> and <code>100</code>.
   */
  default double getCoverageRate() {
    return (getCoveredYieldPoints() * 100.0) / (double) getTotalYieldPoints();
  }
}
