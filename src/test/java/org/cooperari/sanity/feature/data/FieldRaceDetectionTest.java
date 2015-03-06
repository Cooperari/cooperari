package org.cooperari.sanity.feature.data;


import static org.cooperari.CSystem.cHotspot;

import org.cooperari.CSystem;
import org.cooperari.config.CRaceDetection;
import org.cooperari.config.CSometimes;
import org.cooperari.errors.CRaceError;
import org.cooperari.junit.CJUnitRunner;
import org.cooperari.sanity.feature.Data;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
@CRaceDetection(value=true,throwErrors=true)
public class FieldRaceDetectionTest {

  static Data SHARED = new Data();
  
  private static final Runnable WRITER = 
    new Runnable() {
      @Override
      public void run() {
        try {
          SHARED.x = 1;  
        } catch(CRaceError e) {
          cHotspot("writer");
        }
      } 
    };
    private static final Runnable READER = 
        new Runnable() {
          @SuppressWarnings("unused")
          @Override
          public void run() {
            try {
              int x = SHARED.x;
            } catch(CRaceError e) {
              cHotspot("reader");
            }
          } 
        };

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
