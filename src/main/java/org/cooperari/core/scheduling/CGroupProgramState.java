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

package org.cooperari.core.scheduling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

  @Override
  public int size() {
    return _rGroups.size() + _bGroups.size();
  }

  @Override
  public int threads() {
    return _threadCount;
  }

  @Override
  public List<? extends CElement> readyElements() {
    return _rGroups;
  }

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
    final Object[][] rSig = new Object[_rGroups.size()][];
    final Object[][] bSig = new Object[_bGroups.size()][];
    for (int i=0; i < rSig.length; i++) {
      rSig[i] = toArray(_rGroups.get(i));
    }
    for (int i=0; i < bSig.length; i++) {
      bSig[i] = toArray(_bGroups.get(i));
    }
    Arrays.sort(rSig, GCOMPARATOR);
    Arrays.sort(bSig, GCOMPARATOR);
    return new CRawTuple(rSig, bSig); 
  }

  @SuppressWarnings("javadoc")
  private Object[] toArray(Group g) {
    return new Object[] { g._threads.size(), g._location };
  }

  @SuppressWarnings("javadoc")
  private static final Comparator<Object[]> GCOMPARATOR = new Comparator<Object[]>() {

    @Override
    public int compare(Object[] o1, Object[] o2) {
      CThreadLocation l1 = (CThreadLocation) o1[1];
      CThreadLocation l2 = (CThreadLocation) o2[1];
      int cmp = l1.compareTo(l2);
      if (cmp == 0) {
        cmp = ((Integer) o1[0]) - ((Integer) o2[0]);
      }
      return cmp;
    }

  };

}

