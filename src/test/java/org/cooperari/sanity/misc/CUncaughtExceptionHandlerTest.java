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

package org.cooperari.sanity.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.cooperari.core.CUncaughtExceptionHandler;
import org.cooperari.errors.CCheckedExceptionError;
import org.cooperari.errors.CMultipleExceptionsError;
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
    testThrow(InterruptedException.class, new InterruptedException());
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
      if (e instanceof CCheckedExceptionError) {
        e = e.getCause();
      }
      assertSame("exception class", expected, e.getClass());
      
    }
  }
}
