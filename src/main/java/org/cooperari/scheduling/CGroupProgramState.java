package org.cooperari.scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.cooperari.core.util.CRawTuple;

/**
 * Program state representation returned by {@link CProgramStateFactory#GROUP}.
 * 
 * @see CProgramStateFactory#GROUP
 * @since 0.2
 *
 */
class CGroupProgramState implements CProgramState {


  @SuppressWarnings("javadoc")
  private static class Group implements CProgramState.CElement {

    private final int _index;
    private final CThreadLocation _location;
    private final ArrayList<CThreadHandle> _threads = new ArrayList<>();

    Group(int index, CThreadLocation location) {
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
  private final ArrayList<Group> _rGroups = new ArrayList<>();

  /**
   * Blocked thread groups.
   */
  private final ArrayList<Group> _bGroups = new ArrayList<>();

  /**
   * Thread count.
   */
  private final int _threadCount;


  /**
   * Constructor.
   * @param readyThreads Ready threads.
   * @param blockedThreads Blocked threads.
   */
  public CGroupProgramState(List<? extends CThreadHandle> readyThreads, List<? extends CThreadHandle> blockedThreads)  {
    _threadCount = readyThreads.size() + blockedThreads.size(); 
    init(readyThreads, _rGroups);
    init(blockedThreads, _bGroups);
  }

  @SuppressWarnings("javadoc")
  private void
  init(List<? extends CThreadHandle> list, List<Group> elemList) {
    HashMap<CThreadLocation, Group> map = new HashMap<>();
    for (CThreadHandle th : list ) {
      Group g = map.get(th.getLocation());
      if (g == null) {
        int index = elemList.size();
        g = new Group(index, th.getLocation());
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
    return _rGroups.size() + _bGroups.size();
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
    return _rGroups;
  }
  /**
   * @{inheritDoc}
   */
  @Override
  public List<? extends CElement> blockedElements() {
    return _bGroups;
  }
  /**
   * Select a random ready thread.
   * @return A thread from {@link #readyElements()}.
   */
  @Override
  public CThreadHandle select(Random rng) {
    return select(rng.nextInt(_rGroups.size()), rng);
  }
  /**
   * Select specific thread from state.
   * @return The <code>index</code>-th thread from {@link #readyElements()}.
   */
  @Override
  public CThreadHandle select(int index, Random rng) {
    ArrayList<CThreadHandle> list = _rGroups.get(index)._threads;
    return list.get(rng.nextInt(list.size()));
  }

  /**
   * Get signature.
   * @return Signature for the state.
   */
  public Object getSignature() {
   final Object[] rSig = new Object[_rGroups.size()];
   final Object[] bSig = new Object[_bGroups.size()];
   for (int i=0; i < rSig.length; i++) {
     rSig[i] = toArray(_rGroups.get(i));
   }
   for (int i=0; i < bSig.length; i++) {
     bSig[i] = toArray(_bGroups.get(i));
   }
   return new CRawTuple(rSig, bSig);
  }

  @SuppressWarnings("javadoc")
  private Object[] toArray(Group g) {
    ArrayList<CThreadHandle> threads = g._threads;
    Object[] r = new Object[threads.size() * 2];
    int i = 0, j = 0;
    while (i < r.length) {
      CThreadHandle t = threads.get(j++);
      r[i++] = t.getCID();
      r[i++] = t.getLocation();
    }
    return r;
  }

}

