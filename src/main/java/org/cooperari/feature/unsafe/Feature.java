package org.cooperari.feature.unsafe;

import java.util.List;

import org.cooperari.core.CustomYieldPoint;
import org.cooperari.core.FeatureHandler;

/**
 * Feature handler for yield points defined by calls to methods in the <code>sun.misc.Unsafe</code> class.
 * 
 * @since 0.2
 *
 */
public class Feature implements FeatureHandler {
  /**
   * @{inheritDoc}
   */
  @Override
  public void getCustomYieldPoints(List<CustomYieldPoint> list) {
    list.add(new CustomYieldPoint(sun.misc.Unsafe.class));
  }
}

