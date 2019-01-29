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

package org.cooperari.core;


import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

import org.cooperari.errors.CCheckedExceptionError;
import org.cooperari.errors.CMultipleExceptionsError;

/**
 * Handler for uncaught thread exceptions.
 * 
 * <p>
 * Objects of this type may aggregate exceptions from several threads. The
 * aggregated exceptions may be thrown back or wrapped up in a convenient exception type by calling
 * {@link #rethrowExceptionsIfAny}.
 * </p>
 * 
 * @since 0.2
 */
public final class CUncaughtExceptionHandler implements UncaughtExceptionHandler {

  /**
   * List holding exceptions thrown.
   */
  private final ArrayList<Throwable> _exceptions = new ArrayList<>();

  /**
   * Constructs a new uncaught exception handler.
   */
  public CUncaughtExceptionHandler() {

  }

  /**
   * Record an uncaught exception.
   * 
   * @param t Thread.
   * @param e Throwable object.
   * @see UncaughtExceptionHandler#uncaughtException(Thread, Throwable)
   */
  @Override
  public void uncaughtException(Thread t, Throwable e) {
    synchronized (_exceptions) {
      _exceptions.add(e);
    }
  }

  /**
   * Get exception count.
   * 
   * @return The number of exceptions caught by this handler.
   */
  public int getExceptionCount() {
    return _exceptions.size();
  }

  /**
   * Throw uncaught exceptions, if any.
   * <p>
   * The internal exception buffer is cleared, and previously recorded exceptions may be thrown or wrapped
   * up using other exception types, as follows:</p>
   * <ul>
   * <li>If no exceptions are recorded, the method does nothing.</li>
   * <li>If exactly one exception is recorded then:
   * <ul>
   * <li>The exception is directly re-thrown if it is an instance of
   * {@link Error} or {@link RuntimeException} (one of the unchecked exception
   * types).</li>
   * <li>Otherwise the exception at stake must be checked ((note that this
   * should never happen but the case is covered in any case), the exception is
   * wrapped up as the cause for a {@link CCheckedExceptionError} exception that is
   * thrown. .</li>
   * </ul>
   * </li>
   * <li>If more than one exception is recorded, a
   * {@link CMultipleExceptionsError} exception is thrown grouping
   * information for all exceptions.</li>
   * </ul>
   * 
   */
  public void rethrowExceptionsIfAny()  {
    synchronized (_exceptions) {
      if (_exceptions.isEmpty()) {
        return;
      }
      if (_exceptions.size() == 1) {
        // One exception
        Throwable e = _exceptions.get(0);
        _exceptions.clear();
        if (e instanceof RuntimeException) {
          throw (RuntimeException) e;
        }
        if (e instanceof Error) {
          throw (Error) e;
        }
        throw new CCheckedExceptionError(e);
      } 
      // More than one exception ...
      ArrayList<Throwable> copy = new ArrayList<>(_exceptions);
      _exceptions.clear();
      throw new CMultipleExceptionsError(copy);
    }
  }
}

