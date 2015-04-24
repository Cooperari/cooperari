package org.cooperari.scheduling;

import java.util.Arrays;


/**
 * Generic implementation of program state signatures.
 * @since 0.2 edrdo
 *
 */
final class CSignatureImpl implements CProgramState.Signature {
  
  /**
   * Array of objects defining the signature.
   */
  private final Object[] _sig;
  
  /**
   * Cached hash code (computed at construction time).
   */
  private final int _hash;
  
  /**
   * Constructs a new signature.
   * @param args Signature arguments.
   */
  CSignatureImpl(Object ...args) {
    _sig = args;
    _hash = Arrays.deepHashCode(args);
  }
  
  /**
   * Get hash code.
   * @return Integer value in line with the contract of {@link Object#hashCode()}.
   */
  @Override
  public int hashCode() {
    return _hash;
  }

  /**
   * Test equality.
   * @return Boolean value in line with the contract of {@link Object#equals(Object)}.
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o.getClass() != CSignatureImpl.class) {
        return false;
    }
    CSignatureImpl other = (CSignatureImpl) o;
    return _hash == other._hash && Arrays.deepEquals(_sig, other._sig);
  }

}
