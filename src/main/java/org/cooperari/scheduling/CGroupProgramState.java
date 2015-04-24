package org.cooperari.scheduling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.RandomAccess;

/**
 * Program state representation returned by {@link CProgramStateFactory#GROUP}.
 * 
 * @see CProgramStateFactory#GROUP
 * @since 0.2
 *
 */
class CGroupProgramState implements CProgramState {


  @SuppressWarnings("javadoc")
  private static class Element implements CProgramState.CElement {

    private final int _index;
    private final CThreadLocation _location;
    private final ArrayList<CThreadHandle> _threads = new ArrayList<>();

    Element(int index, CThreadLocation location) {
      _index = index;
      _location = location;
    }

    @Override
    public int getCID() {
      return _index;
    }

    @Override
    public CThreadLocation getLocation() {
      return _location;
    }

  }


  /**
   * Ready thread groups.
   */
  private final ArrayList<Element> _rElements = new ArrayList<>();

  /**
   * Blocked thread groups.
   */
  private final ArrayList<Element> _bElements = new ArrayList<>();

  /**
   * Cached thread count.
   */
  private final int _threadCount;


  /**
   * Constructor.
   * @param readyThreads Ready threads.
   * @param blockedThreads Blocked threads.
   */
  public CGroupProgramState(List<? extends CThreadHandle> readyThreads, List<? extends CThreadHandle> blockedThreads)  {
    _threadCount = readyThreads.size() + blockedThreads.size(); 
    init(readyThreads, _rElements);
    init(blockedThreads, _bElements);
  }

  @SuppressWarnings("javadoc")
  private void
  init(List<? extends CThreadHandle> list, List<Element> elemList) {
    HashMap<CThreadLocation, Element> map = new HashMap<>();
    for (CThreadHandle th : list ) {
      Element g = map.get(th.getLocation());
      if (g == null) {
        int index = elemList.size();
        g = new Element(index, th.getLocation());
        map.put(th.getLocation(), g);
        elemList.add(g);
      } 
      g._threads.add(th);
    }
  }


  /**
   * @{inheritDoc}
   */
  @Override
  public int size() {
    return _rElements.size() + _bElements.size();
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public int threads() {
    return _threadCount;
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public List<? extends CElement> readyElements() {
    return _rElements;
  }
  /**
   * @{inheritDoc}
   */
  @Override
  public List<? extends CElement> blockedElements() {
    return _bElements;
  }
  /**
   * Select a random ready thread.
   * @return A thread from {@link #readyElements()}.
   */
  @Override
  public CThreadHandle select(Random rng) {
    return select(rng.nextInt(_rElements.size()), rng);
  }
  /**
   * Select specific thread from state.
   * @return The <code>index</code>-th thread from {@link #readyElements()}.
   */
  @Override
  public CThreadHandle select(int index, Random rng) {
    ArrayList<CThreadHandle> list = _rElements.get(index)._threads;
    return list.get(rng.nextInt(list.size()));
  }

  /**
   * Get signature.
   * @return Signature for the state.
   */
  public CProgramState.Signature getSignature() {
    return null;
  }
  /*
  @SuppressWarnings("javadoc")
  private static class Signature implements CProgramState.Signature {
    final Object[] _signature;
    final int _hash;
    Signature(List<? extends CThreadHandle> readyThreads, List<? extends CThreadHandle> blockedThreads) {
      assert readyThreads instanceof RandomAccess;
      assert blockedThreads instanceof RandomAccess;
      final int rLen = readyThreads.size();
      final int bLen = blockedThreads.size();
      int[] rIds = new int[rLen];
      int[] bIds = new int[bLen];
      CThreadLocation[] locations = new CThreadLocation[rLen + bLen];
      for (int pos = 0; pos != rLen; pos++) {
        CThreadHandle h = readyThreads.get(pos);
        rIds[pos] = h.getCID();
        locations[pos] = h.getLocation();
      }
      for (int pos = 0; pos != bLen; pos++) {
        CThreadHandle h = readyThreads.get(pos);
        bIds[pos] = h.getCID();
        locations[rLen + pos] = h.getLocation();
      }
      _signature = new Object[]{ bIds, rIds, locations };
      _hash = Arrays.deepHashCode(_signature);
    }
    @Override
    public int hashCode() {
      return _hash;
    }
    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o.getClass() != Signature.class) {
          return false;
      }
      Signature other = (Signature) o;
      return _hash == other._hash && Arrays.deepEquals(_signature, other._signature);
    }
   */
}

