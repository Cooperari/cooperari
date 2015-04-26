package org.cooperari.sanity.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.cooperari.core.util.Graph;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
public class GraphTest {

  private Graph<Integer> _graph;
  
  @Before
  public void createGraph() {
    _graph = new Graph<>();
  }
  
  @Test
  public final void testCreation() {
    assertEquals(0, _graph.edges());
  }

  @Test
  public final void testEdgeAdition() {
    assertTrue(_graph.addEdge(0,1));
    assertEquals(1, _graph.edges());
    assertTrue(_graph.addEdge(0,2));
    assertEquals(2, _graph.edges());
    assertTrue(_graph.hasEdge(0,1));
    assertTrue(_graph.hasEdge(0,2));
    assertFalse(_graph.addEdge(0,1));
    assertEquals(2, _graph.edges());
    assertFalse(_graph.addEdge(0,2));
    assertEquals(2, _graph.edges());
  }
  
  @Test
  public final void testEdgeRemoval() {
    testEdgeAdition();
    assertTrue(_graph.removeEdge(0,1));
    assertEquals(1, _graph.edges());
    assertTrue(_graph.removeEdge(0,2));
    assertEquals(0, _graph.edges());
    assertFalse(_graph.removeEdge(0,2));
    assertEquals(0, _graph.edges());
  }

  @Test 
  public final void testCyclesCase1() {
    _graph.addEdge(0, 1);
    _graph.addEdge(1, 2);
    _graph.addEdge(2, 3);
    _graph.addEdge(3, 4);
    _graph.addEdge(4, 0);
    _graph.addEdge(4, 5);
    _graph.addEdge(5, 6);
    
    assertTrue(_graph.hasCycles());
    assertTrue(_graph.hasCycles(0));
    assertTrue(_graph.hasCycles(1));
    assertTrue(_graph.hasCycles(2));
    assertTrue(_graph.hasCycles(3));
    assertTrue(_graph.hasCycles(4));
    assertFalse(_graph.hasCycles(5));
    assertFalse(_graph.hasCycles(6));
  }
  @Test 
  public final void testCyclesCase2() {
    _graph.addEdge(0, 1);
    _graph.addEdge(1, 0);
    _graph.addEdge(2, 0);
    _graph.addEdge(2, 3);
    _graph.addEdge(3, 4);
    _graph.addEdge(5, 6);
    _graph.addEdge(6, 5);
    assertTrue(_graph.hasCycles());
    assertTrue(_graph.hasCycles(0));
    assertTrue(_graph.hasCycles(1));
    assertTrue(_graph.hasCycles(2));
    assertFalse(_graph.hasCycles(3));
    assertFalse(_graph.hasCycles(4));
    assertTrue(_graph.hasCycles(5));
    assertTrue(_graph.hasCycles(6));
  }
  @Test 
  public final void testCyclesCase3() {
    _graph.addEdge(0, 1);
    _graph.addEdge(0, 2);
    _graph.addEdge(1, 3);
    _graph.addEdge(1, 4);
    _graph.addEdge(3, 5);
    _graph.addEdge(3, 6);
    _graph.addEdge(5, 7);
    _graph.addEdge(5, 8);
    _graph.addEdge(5, 9);
    assertFalse(_graph.hasCycles());
    for (int i=0; i <= 9; i++)
      assertFalse(_graph.hasCycles(i));
  }
  
  @Test 
  public final void testFindCyclesCase1() {
    _graph.addEdge(0, 1);
    _graph.addEdge(1, 2);
    _graph.addEdge(2, 3);
    _graph.addEdge(3, 4);
    _graph.addEdge(4, 0);
    _graph.addEdge(4, 5);
    _graph.addEdge(5, 6);
    _graph.addEdge(7, 8);
    _graph.addEdge(8, 9);
    _graph.addEdge(9, 10);
    _graph.addEdge(10, 0);
    assertPath(_graph.findCycle(0), 0, 1, 2, 3, 4, 0);
    assertPath(_graph.findCycle(1), 1, 2, 3, 4, 0, 1);
    assertPath(_graph.findCycle(2), 2, 3, 4, 0, 1, 2);
    assertPath(_graph.findCycle(3), 3, 4, 0, 1, 2, 3);
    assertPath(_graph.findCycle(4), 4, 0, 1, 2, 3, 4);
    assertPath(_graph.findCycle(5));
    assertPath(_graph.findCycle(6));
    assertPath(_graph.findCycle(7), 0, 1, 2, 3, 4, 0);
    assertPath(_graph.findCycle(8), 0, 1, 2, 3, 4, 0);
    assertPath(_graph.findCycle(9), 0, 1, 2, 3, 4, 0);
    assertPath(_graph.findCycle(10), 0, 1, 2, 3, 4, 0);

  }
  private void assertPath(List<Integer> list, Integer... path) {
    int i = 0;
    assertEquals(path.length, list.size());
    for (Integer node : list) {
      assertEquals(String.format("start=%d pos=%d", path[0], i),
                   path[i], node);
      i++;
    }
  }

  @Test 
  public final void testFindCyclesCase2() {
    _graph.addEdge(0, 1);
    _graph.addEdge(1, 0);
    _graph.addEdge(2, 0);
    _graph.addEdge(2, 3);
    _graph.addEdge(3, 4);
    _graph.addEdge(5, 6);
    _graph.addEdge(6, 5);
    assertPath(_graph.findCycle(0), 0, 1, 0);
    assertPath(_graph.findCycle(1), 1, 0, 1);
    assertPath(_graph.findCycle(2), 0, 1, 0);
    assertPath(_graph.findCycle(3));
    assertPath(_graph.findCycle(4));
    assertPath(_graph.findCycle(5), 5, 6, 5);
    assertPath(_graph.findCycle(6), 6, 5, 6);
  }
  @Test 
  public final void testFindCyclesCase3() {
    _graph.addEdge(0, 1);
    _graph.addEdge(0, 2);
    _graph.addEdge(1, 3);
    _graph.addEdge(1, 4);
    _graph.addEdge(3, 5);
    _graph.addEdge(3, 6);
    _graph.addEdge(5, 7);
    _graph.addEdge(5, 8);
    _graph.addEdge(5, 9);
    assertFalse(_graph.hasCycles());
    for (int i=0; i <= 9; i++) {
      assertPath(_graph.findCycle(i));
    }
  }
}
