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
public class CApplication implements CTest {

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
  private String[] _args;
  
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
