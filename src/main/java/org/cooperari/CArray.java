//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.cooperari;

/**
 * Utility methods for array access yield points.
 * 
 * <p>
 * Array access yield points cannot be captured using the (standard) AspectJ compiler. To
 * counter for this limitation, the class provides {@code cRead()} and
 * {@code cWrite()} methods that should replace a direct array access in application code when
 * desirable, i.e., when an array access should be considered a yield point.
 * </p>
 * 
 * <p>
 * The {@code cRead()} and {@code cWrite()} methods execute regular array
 * reads/writes in any case, even for call sites that are not instrumented and/or not running cooperatively.
 * </p>
 * 
 * @since 0.2
 */
public final class CArray {

  /**
   * Read from an array of type <code>boolean</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @return Value of <code>a[i]</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static boolean cRead(boolean[] a, int i) {
    return a[i];
  }

  /**
   * Write to an array of type <code>boolean</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @param v Value to store.
   * @return Value <code>v</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static boolean cWrite(boolean[] a, int i, boolean v) {
    return a[i] = v;
  }

  /**
   * Read from an array of type <code>byte</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @return Value of <code>a[i]</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static byte cRead(byte[] a, int i) {
    return a[i];
  }

  /**
   * Write to an array of type <code>byte</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @param v Value to store.
   * @return Value <code>v</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static byte cWrite(byte[] a, int i, byte v) {
    return a[i] = v;
  }

  /**
   * Read from an array of type <code>short</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @return Value of <code>a[i]</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static short cRead(short[] a, int i) {
    return a[i];
  }

  /**
   * Write to an array of type <code>short</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @param v Value to store.
   * @return Value <code>v</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static short cWrite(short[] a, int i, short v) {
    return a[i] = v;
  }

  /**
   * Read from an array of type <code>char</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @return Value of <code>a[i]</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static char cRead(char[] a, int i) {
    return a[i];
  }

  /**
   * Write to an array of type <code>char</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @param v Value to store.
   * @return Value <code>v</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static char cWrite(char[] a, int i, char v) {
    return a[i] = v;
  }

  /**
   * Read from an array of type <code>int</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @return Value of <code>a[i]</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static int cRead(int[] a, int i) {
    return a[i];
  }

  /**
   * Write to an array of type <code>int</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @param v Value to store.
   * @return Value <code>v</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static int cWrite(int[] a, int i, int v) {
    return a[i] = v;
  }

  /**
   * Read from an array of type <code>long</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @return Value of <code>a[i]</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static long cRead(long[] a, int i) {
    return a[i];
  }

  /**
   * Write to an array of type <code>long</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @param v Value to store.
   * @return Value <code>v</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static long cWrite(long[] a, int i, long v) {
    return a[i] = v;
  }

  /**
   * Read from an array of type <code>float</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @return Value of <code>a[i]</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static float cRead(float[] a, int i) {
    return a[i];
  }

  /**
   * Write to an array of type <code>float</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @param v Value to store.
   * @return Value <code>v</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static float cWrite(float[] a, int i, float v) {
    return a[i] = v;
  }

  /**
   * Read from an array of type <code>double</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @return Value of <code>a[i]</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static double cRead(double[] a, int i) {
    return a[i];
  }

  /**
   * Write to an array of type <code>double</code>.
   * 
   * @param a Array.
   * @param i Index.
   * @param v Value to store.
   * @return Value <code>v</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static double cWrite(double[] a, int i, double v) {
    return a[i] = v;
  }

  /**
   * Read an array of object references.
   * 
   * @param <T> Array type.
   * @param a Array.
   * @param i Index.
   * @return Value of <code>a[i]</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static <T> T cRead(T[] a, int i) {
    return a[i];
  }

  /**
   * Write to an array of object references.
   * 
   * @param <T> Array type.
   * @param a Array.
   * @param i Index.
   * @param v Value to store.
   * @return Value <code>v</code>.
   * @throws NullPointerException If <code>a</code> is <code>null</code>
   * @throws ArrayIndexOutOfBoundsException If <code>i</code> is an invalid
   *         index.
   */
  public static <T> T cWrite(T[] a, int i, T v) {
    return a[i] = v;
  }
  
  /**
   * Private constructor to avoid instantiation.
   */
  private CArray() {
    
  }
}
