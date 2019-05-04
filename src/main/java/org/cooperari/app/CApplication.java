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

package org.cooperari.app;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cooperari.CTest;
import org.cooperari.errors.CCheckedExceptionError;
import org.cooperari.errors.CConfigurationError;
import org.cooperari.errors.CInternalError;

/**
 * Cooperative test wrapper for Java applications.
 * 
 * @since 0.2
 *
 */
public final class CApplication implements CTest {

  /**
   * Class for the Java program.
   */
  private final Class<?> _clazz;
  
  /**
   * Main method handle.
   */
  private final Method _main;
  
  /**
   * Program arguments.
   */
  private final String[] _args;
  
  /**
   * Constructor.
   * @param clazz Application class.
   * @param args Application arguments.
   */
  public CApplication(Class<?> clazz, String[] args) {
    _clazz = clazz;
    _args = args;
    try {
      _main = _clazz.getDeclaredMethod("main", String[].class);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new CConfigurationError("No main method found in " + clazz.getName(), e);
    }
  }
  
  @Override
  public String getName() {
    return _clazz.getName();
  }

  @Override
  public String getSuiteName() {
    return _clazz.getName();
  }

  @Override
  public AnnotatedElement getConfiguration() {
    return _main;
  }
  
  @Override
  public void run() {
    try {
      try {
        _main.invoke(null, (Object) _args);
      } catch (IllegalAccessException | IllegalArgumentException e) {
        throw new CInternalError(e);
      } catch (InvocationTargetException e) {
        throw e.getCause();
      }
    }
    catch(RuntimeException|Error e) {
      throw e;
    }
    catch (Throwable e) {
      throw new CCheckedExceptionError(e);
    }
  }
}
