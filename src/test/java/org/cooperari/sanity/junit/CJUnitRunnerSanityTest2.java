package org.cooperari.sanity.junit;

import static org.junit.Assert.assertEquals;

import org.cooperari.CMaxTrials;
import org.cooperari.junit.CIgnore;
import org.cooperari.junit.CJUnitRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({ "javadoc" })
@RunWith(CJUnitRunner.class)
public class CJUnitRunnerSanityTest2 {
  
  @Test
  @CMaxTrials(2)
  public void testC1() {

  }

  @Test
  @CMaxTrials(2)
  public void testC2() {
    
  }

  @Test
  @CMaxTrials(2)
  public void testC3() {
    
  }
  
  @Test
  @CIgnore
  @CMaxTrials(2) 
  public void testNC1() {
    
  }
  @Test
  @CIgnore
  @CMaxTrials(2)
  public void testNC2() {
    
  }
  
  private static final int C_TEST_METHODS = 3; // testC1, testC2, testC3
  private static final int C_TEST_METHODS_WITH_CIGNORE = 2; // testNC1, testNC2
  private static int setupBeforeClassCounter = 0;
  private static int setupCounter = Integer.MAX_VALUE;
  private static int tearDownCounter = Integer.MAX_VALUE;

  @BeforeClass
  public static void setUpBeforeClass() {
    setupBeforeClassCounter++;
    setupCounter = 0;
    tearDownCounter = 0;
  }
  
  @Before
  public void setUp() throws Exception {
    setupCounter++;
  }

  @After
  public void tearDown() throws Exception {
    tearDownCounter++;
    assertEquals("setup vs. teardown", tearDownCounter, setupCounter);
  }


  @AfterClass
  public static void tearDownAfterClass() {
    assertEquals("setup counter", C_TEST_METHODS * 2 + C_TEST_METHODS_WITH_CIGNORE, setupCounter);
    assertEquals("teardown counter", C_TEST_METHODS * 2 + C_TEST_METHODS_WITH_CIGNORE, tearDownCounter);
    assertEquals("class setup counter", 1, setupBeforeClassCounter);
  }

  

}
