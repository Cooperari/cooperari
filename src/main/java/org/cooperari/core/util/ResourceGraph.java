package org.cooperari.core.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: implementation note w.r.t. IdentityHashMap
// TODO: cycle handling can be simplified.
/**
 * Generic class for graphs.
 * 
 * 
 * @param <T> Type for nodes.
 *  
 * @since 0.2
 *
 */
public final class ResourceGraph<T> {

  /**
   * Sucessor list. 
   */
  private final Map<T, Set<T>> _edges = new IdentityHashMap<>();


  /**
   * Get number of edges.
   * @return Number of edges.
   */
  public int edges() {
    int count = 0;
    for (Set<T> set : _edges.values()) {
      if (set != null) {
        count += set.size();
      }
    }
    return count;
  }
  

  /**
   * Add an edge.
   * @param from Source node.
   * @param to Destination node.
   * @return true if a new edge was created.
   */
  public boolean addEdge(T from, T to) {
    return successors(from, true).add(to);
  }

  /**
   * Test if an edge exists.
   * @param from Source node.
   * @param to Destination node.
   * @return true if edge exists.
   */
  public boolean hasEdge(T from, T to) {
    return successors(from, false).contains(to);
  }

  /**
   * Remove an edge.
   * @param from Source node.
   * @param to Destination node.
   * @return true if a new edge was created.
   */
  public boolean removeEdge(T from, T to) {
    return successors(from, false).remove(to);
  }

  @SuppressWarnings("javadoc")
  private Set<T> successors(T node, boolean create) {
    Set<T> sucSet = _edges.get(node);
    if (sucSet == null) {
      if (create) {
        sucSet = new HashSet<>();
        _edges.put(node, sucSet);
      } else {
        // Note: returned set is immutable.
        sucSet = Collections.emptySet();
      }
    }
    return sucSet;
  }

  /**
   * Get a read-only set-view of the successors of a node.
   * @param node Node.
   * @return The set-view will contain all node successors.
   */
  public Set<T> successors(T node) {
    return Collections.unmodifiableSet(successors(node,false));
  }

  /**
   * Check if there are cycles in the resource graph
   * starting from a given node.
   * 
   * Implementation note: classical breadth-first algorithm.
   * @param from Node
   * @return true if there are cycles. 
   */
  public boolean hasCycles(T from) {
    HashSet<T> visited = new HashSet<>();
    LinkedList<T> toVisit = new LinkedList<>();
    toVisit.offer(from);
    // Note (edrdo): breadth-first search preferred.
    // In contrast getCycle operates depth first to avoid
    // keeping more than one list as possible cycle.
    do {
      T elem = toVisit.remove();
      Set<T> sucSet = successors(elem, false);
      for (T suc : sucSet) {
        if (visited.contains(suc)) {
          return true;
        }
        visited.add(suc);
        toVisit.offer(suc);
      }
    } while (! toVisit.isEmpty());
    return false; 
  }

  /**
   * Tries to find a cycle in the resource graph.
   * @param node Starting node
   * @return List of nodes originating from source node or
   *    empty list if no cycle found.
   */
  public List<T> findCycle(T node) {
    // Use object both as list (insertion order) and visited record
    // with O(1) cost.
    LinkedHashSet<T> visited = new LinkedHashSet<T>();
    T finalNode = findCycle(node, visited);
    if (finalNode == null) {
      return Collections.emptyList();
    }
    // Path order will be the same as insertion order 
    // guaranteed by LinkedHashSet.
    Iterator<T> itr = visited.iterator();
    while (! itr.next().equals(finalNode)) { 
      // just advance
    }
    // Now build the path corresponding to the cycle.
    LinkedList<T> cycle = new LinkedList<>();
    cycle.add(finalNode);
    while (itr.hasNext()) {
      cycle.add(itr.next());
    }
    cycle.add(finalNode);
    return cycle;
  }

  /**
   * Private method for finding cycles.
   * 
   * Implementation note: classical depth-first with no twists.
   * 
   * If a cycle is detected the method returns the final 
   * node in the cycle and the visited set will contain
   * the current one plus all others (that can be iterated
   * in insertion order). 
   * 
   * @param node Current node.
   * @param visited Visited nodes.
   * @return The final node in the cycle or null if no cycle found
   *   from current node.
   */
  private T findCycle(T node, LinkedHashSet<T> visited) {
    if (visited.contains(node)) {
      return node;
    }
    visited.add(node);
    for (T suc : successors(node, false)) {
      T finalNode = findCycle(suc, visited);
      if (finalNode != null) {
        return finalNode; // cycle found
      }
    }
    visited.remove(node);
    return null; // no cycle
  }
  /**
   * Test if there are cycles in the resource graph.
   * @return true if there are cycles in the resource graph.
   */
  public boolean hasCycles() {
    for (T node : _edges.keySet()) {
      if (hasCycles(node))
        return true;
    }
    return false;
  }

}
