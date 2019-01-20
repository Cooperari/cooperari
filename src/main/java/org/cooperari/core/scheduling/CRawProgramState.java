package org.cooperari.core.scheduling;

import java.util.List;
import java.util.Random;

import org.cooperari.core.util.CRawTuple;

/**
 * Program state representation returned by {@link CProgramStateFactory#RAW}.
 * 
 * @see CProgramStateFactory#RAW
 * @since 0.2
 *
 */
class CRawProgramState implements CProgramState {

  /**
   * Ready thread info.
   */
  private final List<? extends CThreadHandle> _readyThreads;
  
  /**
   * Blocked thread ids.
   */
  private final List<? extends CThreadHandle> _blockedThreads;
  
  
  /**
   * Constructor.
   * @param readyThreads Ready threads.
   * @param blockedThreads Blocked threads.
   */
  public CRawProgramState(List<? extends CThreadHandle> readyThreads, List<? extends CThreadHandle> blockedThreads)  {
    _readyThreads = readyThreads;
    _blockedThreads = blockedThreads;
  }
  
  @Override
  public int size() {
    return threads();
  }

  @Override
  public int threads() {
    return _readyThreads.size() + _blockedThreads.size();
  }

  @Override
  public List<? extends CElement> readyElements() {
    return _readyThreads;
  }

  @Override
  public List<? extends CElement> blockedElements() {
    return _blockedThreads;
  }
  
  /**
   * Select a random ready thread.
   * @return A thread from {@link #readyElements()}.
   */
  @Override
  public CThreadHandle select(Random rng) {
    return _readyThreads.get(rng.nextInt(_readyThreads.size()));
  }

  /**
   * Select specific thread from state.
   * @return The <code>index</code>-th thread from {@link #readyElements()}.
   */
  @Override
  public CThreadHandle select(int index, Random rng) {
    return _readyThreads.get(index);
  }

  /**
   * Get signature.
   * @return Signature for the state.
   */
  public Object getSignature() {
      final int rLen = _readyThreads.size();
      final int bLen = _blockedThreads.size();
      int[] rIds = new int[rLen];
      int[] bIds = new int[bLen];
      CThreadLocation[] locations = new CThreadLocation[rLen + bLen];
      for (int pos = 0; pos != rLen; pos++) {
        CThreadHandle h = _readyThreads.get(pos);
        rIds[pos] = h.getCID();
        locations[pos] = h.getLocation();
      }
      for (int pos = 0; pos != bLen; pos++) {
        CThreadHandle h = _blockedThreads.get(pos);
        bIds[pos] = h.getCID();
        locations[rLen + pos] = h.getLocation();
      }
      return new CRawTuple(rIds, bIds, locations);
    }
  
}
