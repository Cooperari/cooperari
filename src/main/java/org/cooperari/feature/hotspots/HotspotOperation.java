package org.cooperari.feature.hotspots;

import org.cooperari.core.COperation;

/**
 * Operation for hotspot yield points.
 * @since 0.2
 *
 */
final class HotspotOperation extends COperation<Void> {
  /**
   * Constructs a new operation.
   * @param id Hotspot id.
   */
  HotspotOperation(String id) {
    super(id);
  }
  /**
   * Constructs a new  operation.
   * @param id Hotspot id.
   * @param cond Boolean condition value.
   */
  HotspotOperation(String id, boolean cond) {
    super(id, cond);
  }
}
