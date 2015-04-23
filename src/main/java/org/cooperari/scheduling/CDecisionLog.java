package org.cooperari.scheduling;

import java.util.HashSet;


/**
 * Utility class to record scheduling decisions.
 * <p>
 * An object of this kind may be useful for schedulers who wish 
 * to avoid repeated scheduling decisions.
 * </p>
 * <p>
 * The log is simply implemented as set of program states/state elements pairs
 * and provides a single method to query/record decisions (@link {@link #record(CProgramState, org.cooperari.scheduling.CProgramState.CElement)}).
 * </p>
 * 
 * @since 0.2
 */
public final class CDecisionLog {
  
  /**
   * Log data.
   */
  private final HashSet<Decision> _log = new HashSet<>();
  
  /**
   * Record a decision.
   * @param state Program state.
   * @param element Program state element.
   * @return <code>true</code> if 
   */
  public boolean record(CProgramState state, CProgramState.CElement element) {
    Decision d = new Decision(state, element);
    return _log.add(d);
  }
  
  @SuppressWarnings("javadoc")
  private final class Decision {
    private final CProgramState _state;
    private final CProgramState.CElement _element;
    private final int _hash;

    public Decision(CProgramState state, CProgramState.CElement element) {
      _state = state;
      _element = element;
      _hash = _state.hashCode() ^ _element.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Decision))
        return false;
      if (this == o)
        return true;
      Decision other = (Decision) o;
      return   _hash == other._hash 
            && _element.equals(other._element) 
            && _state.equals(other._state);
    }

    @Override
    public int hashCode() {
      return _hash;
    }

  }
}
