package org.cooperari.feature.atomic;

import java.util.List;

import org.cooperari.core.CustomYieldPoint;
import org.cooperari.core.FeatureHandler;

/**
 * Feature handler for yield points defined by calls to methods in the <code>java.util.concurrent.atomic</code> package.
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
    Class<?>[] classes = {
        java.util.concurrent.atomic.AtomicBoolean.class,
        java.util.concurrent.atomic.AtomicInteger.class,
        java.util.concurrent.atomic.AtomicIntegerArray.class,
        java.util.concurrent.atomic.AtomicIntegerFieldUpdater.class,
        java.util.concurrent.atomic.AtomicLong.class,
        java.util.concurrent.atomic.AtomicLongArray.class,
        java.util.concurrent.atomic.AtomicLongFieldUpdater.class,
        java.util.concurrent.atomic.AtomicMarkableReference.class,
        java.util.concurrent.atomic.AtomicReference.class,
        java.util.concurrent.atomic.AtomicReferenceArray.class,
        java.util.concurrent.atomic.AtomicReferenceFieldUpdater.class,
        java.util.concurrent.atomic.AtomicStampedReference.class
    };
    for (Class<?> c : classes) {
      list.add(new CustomYieldPoint(c));
    }
  }
}

