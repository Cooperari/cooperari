package org.cooperari.sanity.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.cooperari.core.util.CRawTuple;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class CRawTupleTest {
  private static final Object[] DATA_1 
     = { "X", 1, new Object[] { "Y", 2, null } };
  private static final Object[] DATA_1_CLONE 
  = { "X", 1, new Object[] { "Y", 2, null } };
  
  private static final Object[] DATA_2
  = { "X", 1, new Object[] { "Y", 2, 3 } };
  
  @Test
  public void testBasic() {
    CRawTuple t = new CRawTuple(DATA_1);
    assertSame("data", t.data(), DATA_1);
    assertEquals("hash code", t.hashCode(), Arrays.deepHashCode(DATA_1));
    assertEquals("string representation", t.toString(), Arrays.deepToString(DATA_1));
  }
 
  @Test 
  public void testEqualsToSelf() {
    CRawTuple t = new CRawTuple(DATA_1);
    assertTrue("equals to self", t.equals(t));
  }
  
  @Test 
  public void testEqualsToEquivalent() {
    CRawTuple t = new CRawTuple(DATA_1);
    CRawTuple t2 = new CRawTuple(DATA_1_CLONE);
    assertTrue("equals to equivalent", t.equals(t2));
  }
  
  @Test 
  public void testEqualsToNonEquivalent() {
    CRawTuple t = new CRawTuple(DATA_1);
    CRawTuple t2 = new CRawTuple(DATA_2);
    assertFalse("not equal", t.equals(t2));
  }
  
  @Test 
  public void testEqualsToNull() {
    CRawTuple t = new CRawTuple(DATA_1);
    assertFalse("not equal", t.equals(null));
  }
  
  @Test 
  public void testEqualsToDifferentTypedObject() {
    CRawTuple t = new CRawTuple(DATA_1);
    assertFalse("not equal", t.equals("a string"));
  }
}
