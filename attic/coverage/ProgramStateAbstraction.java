package org.cooperari.core.coverage;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.cooperari.core.CThread;
import org.cooperari.core.CWorkspace;
import org.cooperari.scheduling.CThreadLocation;


public class ProgramStateAbstraction {
  private final HashMap<CThreadLocation,Integer> _tPC;
  private final int _hash;

  public ProgramStateAbstraction(List<CThread> readyThreads) {
    _tPC = new LinkedHashMap<>(readyThreads.size());
    int h = 0;
    for (int i=0; i < readyThreads.size(); i++) {
      CThreadLocation pc = readyThreads.get(i).location();
      Integer count = _tPC.get(pc);
      if (count == null) {
        count = 1;
      } else {
        count ++;
      }
      _tPC.put(pc, count);
      if (pc != null)
        h =  h ^ pc.hashCode() ;
    }
    _hash = h;
  }
  

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    
    if (!(o instanceof ProgramStateAbstraction))
      return false;

    return _tPC.equals(((ProgramStateAbstraction) o)._tPC); 
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return _hash;
  }
  
  public boolean dumpDebugInfo() {
    assert CWorkspace.debug("=> READY THREAD STATE <=");
    for (Entry<CThreadLocation,Integer> _entry : _tPC.entrySet()) {
      assert CWorkspace.debug("%d: %s", _entry.getValue(), _entry.getKey());
    }
    return true;
  }

}
