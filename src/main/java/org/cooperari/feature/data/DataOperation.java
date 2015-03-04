package org.cooperari.feature.data;

import org.cooperari.core.COperation;

/**
 * Base class for data access operations.
 * @author Eduardo Marques, DI/FCUL, 2014-15.
 *
 */
public abstract class DataOperation extends COperation<Void> {
  /**
   * Constructs the operation.
   * @param theObject Object being accessed.
   * @param dataKey Data key, a <code>String</code> object for field accesses or a <code>Integer</code> object for array accesses.
   */
  public DataOperation(Object theObject, Object dataKey) {
    super(theObject, dataKey);
  }

}
