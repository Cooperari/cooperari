package org.cooperari.sanity.feature.data;

import static org.cooperari.CArray.cRead;
import static org.cooperari.CArray.cWrite;
import static org.cooperari.CSystem.cHotspot;

import org.cooperari.CSometimes;
import org.cooperari.CSystem;
import org.cooperari.config.CRaceDetection;
import org.cooperari.errors.CRaceError;
import org.cooperari.junit.CJUnitRunner;
import org.cooperari.sanity.feature.Data;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
@CRaceDetection(value=true, throwErrors=true)
public class ArrayRaceDetectionTest {

  static Data[] SHARED = new Data[] { 
    new Data(), 
    new Data()
  };

  private static int READER_INDEX;
  private static int WRITER_INDEX;
  
  private static final Runnable WRITER = new Runnable() {

    @Override
    public void run() {
      try {
        cWrite(SHARED, WRITER_INDEX, new Data());
      } catch(CRaceError e) {
        cHotspot("writer");
      }
    } 

  };
  private static final Runnable READER = new Runnable() {
    @Override
    public void run() {
      try {
        @SuppressWarnings("unused")
        int dummy = cRead(SHARED, READER_INDEX).x;
        
      } catch(CRaceError e) {
        cHotspot("reader");
      }
    } 
  };

  @Before
  public void init() {
    READER_INDEX = 0;
    WRITER_INDEX = 0;
  }
  
  @Test
  public void testNoRace() {
    CSystem.cRun(READER);
  }
  @Test
  public void testNoRace2() {
    CSystem.cRun(READER, READER);
  }
  @Test
  public void testNoRace3() {
    CSystem.cRun(READER, READER, READER);
  }
  @Test
  public void testNoRace4() {
    WRITER_INDEX = 1;
    CSystem.cRun(READER, READER, WRITER);
  }
  @Test
  public void testNoRace5() {
    READER_INDEX = 1;
    CSystem.cRun(READER, READER, WRITER);
  }
  @Test
  @CSometimes({"reader", "writer"})
  public void testReadWriteRace() {
    CSystem.cRun(READER, WRITER);
  }

  @Test
  @CSometimes({"reader", "writer"})
  public void testReadWriteRace2() {
    CSystem.cRun(READER, READER, WRITER);
  }

  @Test
  @CSometimes({"writer"})
  public void testWriteWriteRace() {
    CSystem.cRun(WRITER, WRITER);
  }

  @Test
  @CSometimes({"writer"})
  public void testWriteWriteRace2() {
    CSystem.cRun(WRITER, WRITER, WRITER);
  }

  @Test
  @CSometimes({"reader", "writer"})
  public void testReadWriteRace3() {
    CSystem.cRun(READER, READER, READER, WRITER, WRITER, WRITER);
  }
}
