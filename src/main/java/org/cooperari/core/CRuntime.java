package org.cooperari.core;


import java.lang.annotation.Annotation;
import java.util.IdentityHashMap;

import org.cooperari.CSystem;
import org.cooperari.core.util.CLog;
import org.cooperari.errors.CConfigurationError;

/**
 * Cooperari runtime.
 * 
 * <p>
 * This class provides a set of facilities for threads that are part of a
 * Cooperari execution environment. A thread may join the environment to avoid
 * keeping a permanent {@link CRuntime} reference in scope, and instead obtaining a
 * reference using {@link #getRuntime()}. In any case, all the functionality of an
 * environment can be accessed directly, without requiring a thread to associate
 * to it.
 * </p>
 * <p>
 * <b>Summary of functionality:</b></p>
 * <ul>
 *   <li>Environment association - a thread:
 *   <ul>
 *     <li>joins an environment using {@link #join()}.</li>
 *     <li>leaves an environment using {@link #leave()}.</li>
 *     <li>gets the environment it is associated to using {@link #getRuntime()}</li>
 *   </ul>
 * <li>A shared object is:
 *   <ul>
 *     <li>registered using {@link #register(Object)}, taking the object's class
 *     obtained through {@link Object#getClass()} as key, or
 *     {@link #register(Object, Object)} using an arbitrary key</li>
 *     <li>unregistered using {@link #unregister(Object) unregister(Object)} for any
 *     type of key (which can be the original object's class if the object was
 *     registered using {@link #register(Object)})</li>
 *     <li>obtained (while registered) using {@link #get(Class) get(Class)} for
 *     class keys or {@link #get(Object)} for arbitrary keys</li>
 *   </ul>
 *   <li>Cooperari features
 *   <ul>
 *     <li>are initialized using {@link #initFeatures()}
 *     <li>are shutdown using {@link #shutdownFeatures()}
 *   </ul>
 * </ul>
 * 
 * 
 * @since 0.2
 */
public class CRuntime {

  /**
   * Temporary arrangement for logging.
   */
  final static CLog LOG = CLog.SYSTEM_OUT;

  /**
   * Thread-local handle.
   */
  private static final ThreadLocal<CRuntime> TL_HANDLE = new ThreadLocal<>();

  /**
   * Get environment for the current thread.
   * 
   * @return Current environment associated to thread, or <code>null</code> if
   *         no association exists.
   */
  public static CRuntime getRuntime() {
    return TL_HANDLE.get();
  }

  // INTERNAL STATE
  /**
   * Shared objects.
   */
  private final IdentityHashMap<Object, Object> _sharedObjects = new IdentityHashMap<>();

  /**
   * Configuration sources.
   */
  private final CConfiguration _config;

  /**
   * Constructor.
   * @param config Configuration.
   */
  public CRuntime(CConfiguration config) {
    _config = config;
    initFeatures();
  }

  /**
   * Get configuration given by an annotation of a certain type.
   * 
   * @param <C> Annotation type for configuration.
   * @param type Class object for the configuration.
   * @throws CConfigurationError if configuration is not found.
   * @return An annotation object of type {@code T} if the configuration is
   *         defined, or <code>null</code> otherwise.
   * 
   */
  public <C extends Annotation> C getConfiguration(Class<C> type) {
    return _config.get(type); 
  }

  /**
   * Reset the environment. This will shutdown Cooperari features and clear the
   * shared-object store. Thread associations to the environment are not
   * affected.
   */
  public void reset() {
    shutdownFeatures();
    _sharedObjects.clear();
  }

  /**
   * Let the current thread join the environment.
   * 
   * @throws IllegalThreadStateException If current thread already joined this
   *         environment.
   */
  public void join() {
    if (getRuntime() != null) {
      throw new IllegalThreadStateException(
          "An environment is already associated to this thread.");
    }
    TL_HANDLE.set(this);
  }

  /**
   * Let the current thread leave the environment.
   * 
   * @throws IllegalThreadStateException If current thread is not associated to
   *         this environment.
   */
  public void leave() {
    if (getRuntime() == null) {
      throw new IllegalThreadStateException(
          "No environment associated to thread.");
    }
    TL_HANDLE.remove();
  }

  /**
   * Shortcut for calling {@link CEngine#getThread(int)} on the scheduler instance
   * associated to this runtime.
   * 
   * @param index Thread index.
   * @return A {@link CThread} instance.
   */
  public static CThread getThread(int index) {
    return getRuntime().get(CEngine.class).getThread(index);
  }

  /**
   * Register a shared object with this environment.
   * <p>
   * By default the object's class becomes the shared object's key, i.e., a call
   * to <code>register(o)</code> is equivalent to
   * <code>register(o.getClass(), o)</code>. An object registered with this
   * method should be unregistered with a call to
   * <code>unregister(o.getClass())</code>.
   * </p>
   * 
   * @param o Object.
   * @see #register(Object, Object)
   * @see #unregister(Object)
   * @see #get(Object)
   */
  public void register(Object o) {
    register(o.getClass(), o);
  }

  /**
   * Register a shared object with this environment with an associated key.
   * <p>
   * Any previous association with the key will be overridden. An object
   * registered with this method can be accessed through <code>get(key)</code>,
   * until unregistered with a call to
   * <code>unregister(key)</code>. The key object reference
   * must be the same in calls to <code>get()</code> and
   * <code>unregister()</code>.
   * </p>
   * 
   * @param key Key.
   * @param o Object.
   * @see #unregister(Object)
   * @see #get(Object)
   */
  public void register(Object key, Object o) {
    _sharedObjects.put(key, o);
  }

  /**
   * Unregister shared object for given key.
   *
   * @param key Object
   * @see #register(Object, Object)
   * @see #get(Object)
   */
  public void unregister(Object key) {
    _sharedObjects.remove(key);
  }

  /**
   * Get shared object for given key.
   * 
   * @param key Key.
   * @return Registered object reference for given key, or <code>null</code> if
   *         it does not exists.
   * @see #register(Object, Object)
   * @see #unregister(Object)
   */
  public Object get(Object key) {
    return _sharedObjects.get(key);
  }

  /**
   * Get shared object that is registered for the given class.
   * 
   * @param <T> Class of object.
   * @param clazz Class.
   * @return Registered object reference for given class, or <code>null</code>
   *         if it does not exists.
   * @see #register(Object, Object)
   * @see #register(Object, Object)
   * @see #unregister(Object)
   */
  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> clazz) {
    return (T) _sharedObjects.get(clazz);
  }

  /**
   * Initialize features.
   * 
   * @see FeatureHandler#init(CRuntime)
   * @see FeatureHandler
   */
  public void initFeatures() {
    boolean cMode = CSystem.inCooperativeMode();
    for (FeatureHandler fh : Features.getFeatureHandlers()) {
      if (cMode || !fh.cooperativeSemanticsRequired()) {
        assert CWorkspace.debug("initializating feature handler " + fh.getClass());
        fh.init(this);
      }
    }
  }

  /**
   * Shutdown features.
   * 
   * @see FeatureHandler#shutdown(CRuntime)
   * @see FeatureHandler
   */
  public void shutdownFeatures() {
    boolean cMode = CSystem.inCooperativeMode();
    for (FeatureHandler fh : Features.getFeatureHandlers()) {
      if (cMode || !fh.cooperativeSemanticsRequired()) {
        assert CWorkspace.debug("shuting down feature handler " + fh.getClass());
        fh.shutdown(this);
      }
    }
  }
}
