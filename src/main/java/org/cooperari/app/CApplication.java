package org.cooperari.app;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cooperari.CTest;
import org.cooperari.errors.CCheckedExceptionError;
import org.cooperari.errors.CInternalError;

@SuppressWarnings("javadoc")
public class CApplication implements CTest {

  private final Class<?> _clazz;
  private final Method _mainMethod;
  private String[] _args;
  
  public CApplication(Class<?> clazz, String[] args) {
    _clazz = clazz;
    _args = args;
    try {
      _mainMethod = _clazz.getDeclaredMethod("main");
    } catch (NoSuchMethodException | SecurityException e) {
      throw new CInternalError(e);
    }
 
//    int mod = _mainMethod.getModifiers();
//    int mask = Method.PUBLIC | Method.DECLARED;
    
              
    
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
    return _mainMethod;
  }
  
  @Override
  public void run() {
    try {
      try {
        _mainMethod.invoke(null, (Object[]) _args);
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
