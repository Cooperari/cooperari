package org.cooperari.scheduling;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.cooperari.core.CWorkspace;

/**
 * Program state factory.
 * 
 * @since 0.2
 */
public final class CProgramStateFactory {

  /**
   * Factory method a new program state.
   * @param useAbstraction Use program state abstraction.
   * @param readyThreads List of ready threads.
   * @return A new program state.
   */
  public static CProgramState create(boolean useAbstraction, List<? extends CThreadHandle> readyThreads) {
    return new ProgramStateAbstraction(readyThreads);
  }
  /**
   * Private constructor to avoid unintended instantation.
   */
  private CProgramStateFactory() { }
  
  
  @SuppressWarnings("javadoc")
  private static class ProgramStateAbstraction implements CProgramState {
    private final HashMap<CThreadLocation,Integer> _state;
    private final int _threads; 
    private final int _hash;

    public ProgramStateAbstraction(List<? extends CThreadHandle> readyThreads) {
      _state = new LinkedHashMap<>(readyThreads.size());
      _threads = readyThreads.size();
      int h = 0;
      for (int i=0; i < readyThreads.size(); i++) {
        CThreadLocation location = readyThreads.get(i).location();
        Integer count = _state.get(location);
        if (count == null) {
          count = 1;
        } else {
          count ++;
        }
        _state.put(location, count);
        if (location != null)
          h =  h ^ location.hashCode() ;
      }
      _hash = h;
    }
    
    @Override
    public boolean equals(Object o) {
      if (o == this)
        return true;
      
      if (!(o instanceof ProgramStateAbstraction))
        return false;
      
      ProgramStateAbstraction other = (ProgramStateAbstraction) o;
      return _threads == other._threads
          && _hash == other._hash
          && _state.equals(((ProgramStateAbstraction) o)._state); 
    }

    @Override
    public int hashCode() {
      return _hash;
    }
    
    public boolean dumpDebugInfo() {
      assert CWorkspace.debug("=> READY THREAD STATE <=");
      for (Entry<CThreadLocation,Integer> _entry : _state.entrySet()) {
        assert CWorkspace.debug("%d: %s", _entry.getValue(), _entry.getKey());
      }
      return true;
    }


    @Override
    public boolean usesAbstraction() {
      return true;
    }


    @Override
    public int size() {
      return _state.size();
    }


    @Override
    public int threads() {
      return _threads;
    }


    @Override
    public List<? extends CElement> elements() {
      // TODO Auto-generated method stub
      return null;
    }

  }

  private static class CThreadGroupHandleImpl implements CThreadGroupHandle {

    @Override
    public int getCID() {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public CThreadLocation location() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public int size() {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public List<CThreadHandle> threads() {
      // TODO Auto-generated method stub
      return null;
    }
    
  }
}
