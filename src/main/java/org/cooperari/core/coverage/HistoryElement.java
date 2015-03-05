package org.cooperari.core.coverage;

import org.cooperari.core.CThread;


public class HistoryElement {
  
  private static final boolean FULL_ABS = true;
  private final Object _choice;
  private final ProgramStateAbstraction _state;
  private final int _hash;

  public HistoryElement(CThread t, ProgramStateAbstraction state) {
    if (FULL_ABS)
      _choice = t.getYieldPoint();
    else
      _choice = new Integer(t.getCID());
    _state = state;
    _hash = _choice.hashCode() + _state.hashCode();
  }

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof HistoryElement))
      return false;

    if (this == o)
      return true;

    HistoryElement other = (HistoryElement) o;

    return _hash == other._hash && _choice.equals(other._choice) && _state.equals(other._state);
  }

  @Override
  public int hashCode() {
    return _hash;
  }

}