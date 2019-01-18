package org.cooperari.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cooperari.CTest;
import org.cooperari.errors.CInternalError;

public class CApplication implements CTest {

  private final Class<?> _clazz;
  private final Method _mainMethod;
  private String[] _args;
  
  public CApplication(Class<?> clazz, String[] args) {
    _clazz = clazz;
    _args = args;
    try {
      _mainMethod = _clazz.getDeclaredMethod("main", String.class);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new CInternalError(e);
    }
    int mod = _mainMethod.getModifiers();
    int mask = Method.PUBLIC | Method.DECLARED;
    
              
    
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
  public void run() {
    try {
      _mainMethod.invoke(null, (Object[]) _args);
    } catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new CInternalError(e);
    }
  }

}
