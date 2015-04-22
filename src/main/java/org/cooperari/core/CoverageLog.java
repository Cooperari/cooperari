package org.cooperari.core;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.cooperari.CYieldPoint;
import org.cooperari.core.util.CReport;

/**
 * Coverage log.
 * 
 * <p>
 * An object of this kind maintains a log of yield point coverage.
 * </p>
 * 
 * @since 0.2
 *
 */
public final class CoverageLog {

  /**
   * The actual log.
   */
  private final TreeMap<CYieldPoint, Boolean> _log = new TreeMap<>();

  /**
   * Number of covered yield points  (attribute that avoids
   * traversal of the map for the purpose of knowing how many
   * yield points were covered)
   */
  private int _coveredYieldPoints = 0;

  /**
   * Set of files associated to yield points in the log.
   */
  private final HashSet<String> _sourceFiles = new HashSet<>();

  /**
   * Constructs a new coverage log.
   */
  public CoverageLog() { }

  /**
   * Get total number of yield points recorded.
   * 
   * @return Yield point count.
   */
  public int getYieldPointCount() {
    return _log.size();
  }

  /**
   * Get coverage rate.
   * 
   * @return The percentage of yield points covered.
   */
  public double getCoverageRate() {
    return (_coveredYieldPoints * 100.0) / (double) _log.size();
  }

  /**
   * Get total number of yield points covered.
   * 
   * @return Yield point count.
   */
  public int getYieldPointsCovered() {
    return _coveredYieldPoints;
  }

  /**
   * Record definition of yield point.
   * The yield point is not marked as covered. 
   * If the yield point is already defined, the call is ignored and the coverage status is unchanged.
   * @param yp Yield point.
   */
  public void recordDefinition(CYieldPoint yp) {
    if (!_log.containsKey(yp)) {
      assert CWorkspace.debug("DEF %s", yp);
      _log.put(yp, false);
      _sourceFiles.add(yp.getSourceFile());
    }
  }

  /**
   * Mark a yield point as covered.
   * @param yp Yield point.
   */
  public void markAsCovered(CYieldPoint yp) {
    Boolean b = _log.put(yp, true);
    if ( b == null) { 
      _coveredYieldPoints++;
      _sourceFiles.add(yp.getSourceFile());
      CWorkspace.log("COVERED %s", yp);
    } else if (b == false) {
      _coveredYieldPoints++;
      assert CWorkspace.debug("COVERED %s", yp);
    }
  }


  /**
   * Enrich this log with information from other log.
   * 
   * <p>
   * The following contract applies:
   * <ul>
   * <li>This log is enriched only for yield points occurring
   * in source files it already references, but that are not known yet. 
   * Yield points in the other log that related to other source files are ignored.</li>
   * <li>
   * For each yield point of the kind above, the coverage status in the other log
   * may be ignored or not, according to the {@code ignoreStatus} parameter value:
   * <ul>
   * <li> if {@code true}: the yield point will only be marked as defined (uncovered thus), regardless
   * of the coverage status in the other log</li>
   * <li> if {@code false}: the yield point will be marked as defined and also as covered if
   * it is marked as covered in the other log;
   * </li>
   * </ul>
   * </ul>
   * </p>
   * 
   * @param otherLog The other log.
   * @param ignoreCoverageStatus Ignore coverage status from the other log.
   */
  public void enrich(CoverageLog otherLog, boolean ignoreCoverageStatus) {
    assert CWorkspace.debug("ENRICHING log %d %s :: %d %d", otherLog.getYieldPointCount(), ignoreCoverageStatus,  getYieldPointCount(), getYieldPointsCovered());
    for (String sf : _sourceFiles) {
      CYieldPointImpl lowerBound = new CYieldPointImpl("", sf, -1);
      // Note: extra char so that view iterates until the last possible entry
      CYieldPointImpl upperBound = new CYieldPointImpl("", sf + '*', -1);
      Map<CYieldPoint,Boolean> view = otherLog._log.subMap(lowerBound, upperBound);
      for (Map.Entry<CYieldPoint,Boolean> entry : view.entrySet()) {
        CYieldPoint yp = entry.getKey();
        if (ignoreCoverageStatus || !entry.getValue()) {
          recordDefinition(yp);
        } else {
          markAsCovered(yp);
        }
      }
    }
    assert CWorkspace.debug("ENRICHED log :: %d %d", getYieldPointCount(), getYieldPointsCovered());
  }

  
  /**
   * Generate a coverage report.
   * @param reportId Report id.
   * @throws IOException if an I/O error occurs.
   * @return File object for the coverage report.
   */
  public File produceCoverageReport(String reportId) throws IOException {
    CReport r = CWorkspace.INSTANCE.createReport(reportId);
    r.beginSection("GLOBAL STATISTICS", "TOTAL", "COVERED", "%");
    r.writeEntry(getYieldPointCount(), getYieldPointsCovered(),
        getCoverageRate());
    r.beginSection("YIELD POINTS", "C", "SOURCE FILE", "LINE", "SIGNATURE");
    for (Entry<CYieldPoint, Boolean> e : _log.entrySet()) {
      r.writeEntry(e.getValue() ? 'Y' : 'N', 
                   e.getKey().getSourceFile(), 
                   e.getKey().getSourceLine(), 
                   e.getKey().getSignature());
    }
    r.close();
    return r.getFile();
  }

}
