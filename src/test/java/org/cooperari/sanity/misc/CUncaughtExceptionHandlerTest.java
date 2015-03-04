package org.cooperari.sanity.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.cooperari.CInternalError;
import org.cooperari.CMultipleExceptionsError;
import org.cooperari.core.CUncaughtExceptionHandler;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
public class CUncaughtExceptionHandlerTest {

  CUncaughtExceptionHandler handler;
  
  @Before
  public void setup() {
    handler = new CUncaughtExceptionHandler();
  }
  
  @Test 
  public void testNoExceptions() {
    assertEquals("exception count", 0, handler.getExceptionCount());
    try {
      handler.rethrowExceptionsIfAny(); // no effect
    } catch (Throwable e) {
      fail("Unexpected exception " + e);
    }
  }
 
  @Test
  public void testWithRuntimeExceptionInstance() {
    testThrow(NullPointerException.class, new NullPointerException());
  }
  
  @Test
  public void testWithErrorInstance() {
    testThrow(AssertionError.class, new AssertionError());
  }
  
  
  @Test
  public void testWithCheckedExceptionInstance() {
    testThrow(CInternalError.class, new InterruptedException());
  }
  
  
  @Test
  public void testWithMultipleExceptions() {
    testThrow(CMultipleExceptionsError.class, new InterruptedException(), new NullPointerException());
  }

  private void testThrow(Class<? extends Throwable> expected, Throwable... exceptions) {
    for (Throwable e : exceptions) {
      handler.uncaughtException(Thread.currentThread(), e);
    }
    try {
      assertEquals("exception count", exceptions.length, handler.getExceptionCount());
      handler.rethrowExceptionsIfAny();
      fail("Expected exception " + expected);
    }
    catch (Throwable e) {
      assertSame("exception class", expected, e.getClass());
    }
  }
}
