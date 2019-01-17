package org.cooperari.feature.monitor;

import static org.cooperari.core.util.UnsafeVMOperations.UNSAFE;

import java.util.LinkedList;
import java.util.Queue;

import org.cooperari.core.CThread;
import org.cooperari.core.CWorkspace;
import org.cooperari.errors.CInternalError;

/**
 * Representation of a monitor.
 * 
 * @since 0.1
 */
public final class Monitor {
  /**
   * Instance representing an invalid monitor, 
   * when monitor operations involve the null reference.
   */
  public static final Monitor NULL = new Monitor(-1,null);

  /**
   * Instance representing an unreferenced monitor.
   */
  public static final Monitor UNREFERENCED_MONITOR = new Monitor(-1,null);
  
  /**
   * Identifier.
   */
  private final int _id;
  /**
   * Object that this monitor associates to.
   */
  private final Object _theObject;
  /**
   * Owner thread, when locked.
   */
  private CThread _owner = null;
  /**
   * Nested lock count.
   */
  private int _ownerLockCount;
  /**
   * Reference count.
   */
  private int _refCount;
  /**
   * Number of threads waiting on this monitor.
   */
  private int _waitCount;
  /**
   * Notification sequence counter.
   */
  private int _notifyEpoch;
  /**
   * Notification queue.
   */
  private Queue<Integer> _notifications;

  // BASIC METHODS
  /**
   * Constructs a new monitor.
   * @param id Id for the monitor.
   * @param object Object the monitor relates to.
   */
  public Monitor(int id, Object object) {
    _id = id;
    _theObject = object;
    assert CWorkspace.debug("mon ctor| %s", toString());
  }

  /**
   * Get monitor id.
   * @return Monitor id.
   */
  public long getId() {
    return _id;
  }
  
  /**
   * Get object the monitor refers to.
   * @return A <code>Object</code> instance.
   */
  public Object getObject() {
    return _theObject;
  }
  
  // REFERENCE COUNT METHODS
  /**
   * Get reference count.
   * @return A value greater or equal to <code>0</code>.
   */
  public int references() {
    return _refCount;
  }
  /**
   * Increment reference count.
   * @return Updated value of reference count.
   */
  public int addReference() {
    return ++ _refCount ;
  } 
  /**
   * Decrement reference count.
   * @return Updated value of reference count.
   */
  public int removeReference() {
    return -- _refCount;
  }
  
  // OWNERSHIP METHODS
  /**
   * Test if monitor is currently locked.
   * @return true if some thread owns the monitor.
   */
  public boolean isLocked() {
    return _owner != null;
  }
  
  /**
   * Get owner thread.
   * @return Owner thread or null if monitor is not locked.
   */
  public CThread getOwner() {
    return _owner;
  }
  
  /**
   * Get owner lock count
   * @return <code>0</code> if monitor is not locked, a value greater than <code>0</code> otherwise.
   */
  public int getOwnerLockCount() {
    return _ownerLockCount;
  }

  // LOCK ACQUISITION / RELEASE
  /**
   * Enter monitor. 
   * @param t Thread that is allowed to acquire the monitor.
   */
  public void enter(CThread t) {
    if (_owner == null) {
      _owner = t;
      assert _ownerLockCount == 0;
    }
    else if (_owner != t) {
      throw new CInternalError();
    } 
    _ownerLockCount++;
    assert CWorkspace.debug("enter | %s", toString());
  }
  
  /**
   * Exit the monitor 
   */
  public void exit() {
    if (--_ownerLockCount == 0) {
      _owner = null;
    }
    assert CWorkspace.debug("exit | %s", toString());
  }
  /**
   * Relinquish the lock.
   * @param t Thread that must be the owner.
   * @param wait Indicates that calling thread will be on the wait set. 
   * @return Current owner lock count of the thread.
   */
  @SuppressWarnings({ "restriction", "deprecation" })
  public int relinquish(CThread t, boolean wait) {
    if (_owner != t) {
      throw new CInternalError();
    }
    int prevOwnerCount = _ownerLockCount;
    _owner = null;
    _ownerLockCount = 0;
    if (wait) {
      _waitCount++;
    }
    assert CWorkspace.debug("UNSAFE monitorExit()" + toString());
    UNSAFE.monitorExit(_theObject);
    return prevOwnerCount;
  }
  /**
   * Re-acquire the lock.
   * @param t Thread that will reacquire ownership.
   * @param lockCount Lock count to re-establish.
   */
  @SuppressWarnings({ "restriction", "deprecation" })
  public void reacquire(CThread t, int lockCount) {
    if (_owner != null) {
      throw new CInternalError();
    }
    _owner = t;
    _ownerLockCount = lockCount;
    assert CWorkspace.debug("UNSAFE monitorEnter()" + toString());
    UNSAFE.monitorEnter(_theObject);
  }
  // NOTIFICATION HANDLING

  /**
   * Get current notification epoch.
   * @return Current value of the notification sequence number.
   */
  public int getNotifyEpoch() {
    return _notifyEpoch;
  }
  
  /**
   * Consume a notification. 
   * @return Sequence number for removed notification.
   */
  public int consumeNotification() {
    int seq = _notifications.remove();
    assert CWorkspace.debug("consume | %s", toString());
    return seq;
  }


  /**
   * Deliver a notification for one thread waiting on this monitor.
   */
  public void notifyOneThread() {
    if (_waitCount > 0) {
      assert CWorkspace.debug("1 out of %d threads will be awakened", _waitCount);
      if (_notifications == null) {
        _notifications = new LinkedList<>();
      }
      _waitCount --;
      _notifications.offer(_notifyEpoch);
      _notifyEpoch ++;
      assert CWorkspace.debug("notify (1) | %s", toString());
    } else {
      assert CWorkspace.debug("notify (lost) | %s", toString());
    }    
  }
  
  /**
   * Deliver one notification per each thread waiting on this monitor.
   */
  public void notifyAllThreads() {
    if (_waitCount > 0) {
      if (_notifications == null) {
        _notifications = new LinkedList<>();
      }
      for (int i=0; i != _waitCount; i++) {
        _notifications.offer(_notifyEpoch);
      }
      _waitCount = 0;
      _notifyEpoch ++;
      assert CWorkspace.debug("notifyAll | %s", toString());
    } else {
      assert CWorkspace.debug("notifyAll (lost) | %s", toString());
    }    
  }
  
  /**
   * Get available notifications greater or equal than a given sequence number.
   * @param seq Sequence number.
   * @return <code>true</code> iff such a notification exists.
   */
  public boolean availableNotification(int seq) {
    return _notifications != null
        && _notifications.size() > 0 
        && _notifications.peek() >= seq;  
  }

  // COMPLEMENTARY METHODS
  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @SuppressWarnings("javadoc")
  public String toString() {
    return new StringBuilder()
    .append('#')
    .append(_id)
    .append("[O=")
    .append(_owner != null ? _owner.getCID() : -1)
     .append(' ')
    .append("C")
     .append('=')
    .append(_ownerLockCount)
    .append(' ')
    .append('R')
    .append('=')
    .append(_refCount)
    .append(' ')
    .append('W')
    .append('=')
    .append(_waitCount)
    .append(' ')
    .append('N')
    .append('=')
    .append(_notifyEpoch)
    .append(' ')
    .append('Q')
    .append('=')
    .append(_notifications == null ? 0 : _notifications.size())
    .append(']').toString();
  }    
}
