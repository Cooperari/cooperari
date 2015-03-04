package org.cooperari.core;

import java.lang.reflect.Method;

/**
 * Custom yield point definition.
 * 
 * @since 0.2
 */
public final class CustomYieldPoint {
  
  /**
   * Aspect name.
   */
  private final String _aspectName;
  
  /**
   * Pointcut expression.
   */
  private final String _pointcutExpression;

  /**
   * Construct a custom yield point for calls to all declared methods of a given class.
   * @param clazz Class instance.
   */
  public CustomYieldPoint(Class<?> clazz) {
    _aspectName = "org.cooperari.inline_aspects." 
        + clazz.getCanonicalName().replace('.', '_');
    _pointcutExpression = String.format("call(* %s.*(..))", clazz.getCanonicalName());
  }
  
  /**
   * Construct a custom yield point for a Java method call.
   * @param method Method instance.
   */
  public CustomYieldPoint(Method method) {
    _aspectName = "org.cooperari.inline_aspects." 
        + method.getDeclaringClass().getCanonicalName().replace('.', '_')
        + "_"
        + method.getName();
    _pointcutExpression = String.format("call(* %s.%s(*))", method.getDeclaringClass().getCanonicalName(), method.getName());
  }
  
  /**
   * Get aspect class name for this yield point.
   * @return Name of the class to use for the inline aspect.
   */
  public String getAspectName() {
    return _aspectName;
  }
  
  /**
   * Get pointcut expression for yield point.
   * @return A pointcut expression for the yield point.
   */
  public String getPointcutExpression() {
    return _pointcutExpression;
  }

}
