package org.cooperari.sanity.misc;

import static org.junit.Assert.assertEquals;

import org.cooperari.config.CBaseConfiguration;
import org.cooperari.config.CMaxTrials;
import org.cooperari.core.CConfiguration;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({"javadoc","unused"})
public class CConfigurationTest {

  private static final int BASE_CONFIG = CBaseConfiguration.class
      .getDeclaredAnnotation(CMaxTrials.class).value();

  private static class DummyClassA {

    public void foo() {
    }

    @CMaxTrials(1000000)
    public void foo2() {
    }
  }

  @Test
  public void testInheritBaseConfiguration() throws Exception {
    assertConfig(DummyClassA.class, "foo", BASE_CONFIG);
  }
  
  @Test
  public void testMethodSpecificConfiguration() throws Exception {
    assertConfig(DummyClassA.class, "foo2", 1000000);
  }
  
  private static final int DUMMY_CLASS_B_CONFIG = DummyClassB.class
      .getDeclaredAnnotation(CMaxTrials.class).value();
  
  @CMaxTrials(1000000)
  private static class DummyClassB {
    public void foo() {   }

    @CMaxTrials(2000000)
    public void foo2() {
    }
  }
  
  @Test
  public void testInheritDeclaringClassConfiguration() throws Exception {
    assertConfig(DummyClassB.class, "foo", DUMMY_CLASS_B_CONFIG);
  }
  
  @Test
  public void testMethodSpecificConfiguration2() throws Exception {
    assertConfig(DummyClassB.class, "foo2", 2000000);
  }
  
 
  private void assertConfig(Class<?> c, String methodName, int expected) throws Exception {
    CConfiguration config = new CConfiguration(c.getDeclaredMethod(methodName));
    CMaxTrials actual = config.get(CMaxTrials.class);
    assertEquals(
        c.getSimpleName() + "." + methodName + "()", expected, actual.value());
  }

}
