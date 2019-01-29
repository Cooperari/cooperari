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


package org.cooperari.examples;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.LinkedList;

import org.cooperari.CSystem;
import org.cooperari.junit.CJUnitRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import sun.misc.Unsafe;

/**
 * Variant of the {@link AtomicPrimitives} example
 * employing {@link sun.misc.Unsafe}.
 * 
 * @since 0.2
 */
@RunWith(CJUnitRunner.class)
@SuppressWarnings("restriction")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SunMiscUnsafe {

  /**
   * Unsafe handle.
   */
  static Unsafe UNSAFE;
  
  /**
   * Global JUnit test fixture setup (executed just once).
   * It obtains access to the Unsafe singleton instance.
   */
  @BeforeClass 
  public static void globalSetup() {
    try {
      Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
      singleoneInstanceField.setAccessible(true);
      UNSAFE = (Unsafe) singleoneInstanceField.get(null);
    } catch (Exception e) {
      throw new Error(e);
    }
  }
  
  /**
   * Naive busy-wait blocking queue implementation backed 
   * by a (thread-unsafe) linked-list, and {@link sun.misc.Unsafe}
   * operations for access control.
   * 
   * The queue has unlimited capacity but {@link #remove}
   * should block when the queue is empty.
   * 
   * @param <E> Type of items in the queue.
   */
  static class Queue<E> { 
    /**
     * Address of field "access".
     */
    private static final long ACCESS_ADDRESS;

    static {
      try {
        ACCESS_ADDRESS = UNSAFE.objectFieldOffset(Queue.class.getDeclaredField("access"));
      } catch (Exception e) {
        throw new Error(e);
      }
    }
    
    /** Boolean field for access control. */
    volatile Boolean access = true;

    /** Items in the queue (note that LinkedList is not thread-safe). */
    final LinkedList<E> list = new LinkedList<>();

    /**
     * Add an item to the queue.
     * @param item Item to add.
     */
    void add(E item) {
      // Naive, but correct.
      while (! UNSAFE.compareAndSwapObject(this, ACCESS_ADDRESS, true, false)) { }
      list.addLast(item);
      access = true;
    }

    /**
     * Remove an item from the queue.
     * The implementation has an atomicity violation.
     * @return Item remove.
     */
    E remove() {
      E item = null;
      do {
        // Bug: atomicity violation.
        while (! access) { }
        access = false;
        
        if (!list.isEmpty()) {
          item = list.removeFirst();
        }
        
        access = true;
      } while (item == null);
      return item;
    }

      /** 
       * Get size of the queue.
       * @return Size of the queue.
       */
      public int size() {
        // Naive, but correct.
        while (! UNSAFE.compareAndSwapObject(this, ACCESS_ADDRESS, true, false)) { }
        int n = list.size();
        access = true;
        return n;
      }
    }

    /** Queue instance. */
    Queue<Integer> theQueue;

    /**
     * JUnit test fixture, execute before each test. 
     * It creates the queue to be used by tests.
     */
    @Before 
    public void setup() {
      theQueue = new Queue<>();
    }

    /**
     * Simple test for the queue. 
     * 
     * Two threads add an item, two others removes an item.
     * The bug is likely
     * to be exposed even with preemptive semantics.
     */
    @Test
    public void test() {
      CSystem.forkAndJoin(
          () -> { theQueue.add(1); },
          () -> { theQueue.add(2); },
          () -> { theQueue.remove(); },
          () -> { theQueue.remove(); }
      );
      assertEquals(0, theQueue.size());
    }
    
  }
