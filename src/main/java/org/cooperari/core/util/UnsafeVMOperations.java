package org.cooperari.core.util;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * Wrapper class for an instance of <code>sun.misc.Unsafe</code>.
 * 
 * @since 0.2
 */
@SuppressWarnings("restriction")
public class UnsafeVMOperations {
  /**
   * Handle constant for unsafe VM operations.
   */
  public static final Unsafe UNSAFE = getUnsafeInstance();

  /**
   * Private constructor to avoid unintended construction.
   */
  private UnsafeVMOperations() { }

  @SuppressWarnings({"javadoc"})
  private static Unsafe getUnsafeInstance() {
    try {
      Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
      singleoneInstanceField.setAccessible(true);
      return (Unsafe) singleoneInstanceField.get(null);
    } catch (Throwable e) {
      throw new Error(e);
    }
  }
}
