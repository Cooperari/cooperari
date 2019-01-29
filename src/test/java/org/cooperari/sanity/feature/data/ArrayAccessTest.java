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

package org.cooperari.sanity.feature.data;


import static org.cooperari.CArray.cRead;
import static org.cooperari.CArray.cWrite;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.cooperari.CSystem;
import org.cooperari.junit.CJUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
@RunWith(CJUnitRunner.class)
public class ArrayAccessTest {

  public static final Runnable rArrayAccess = new Runnable() {
 
    public void run() {
      Object[]  o = { new Object(), new Object()};
      String[]  S = { "a", "b" };
      boolean[] B = { false, true };
      byte[]    b = { 0, 1 };
      char[]    c = { 0, 1 };
      short[]   s = { 0, 1 };
      int[]     i = { 0, 1 };
      long[]    l = { 0, 1 };
      float[]   f = { 0, 1 };
      double[]  d = { 0, 1 };

      cWrite(o, 0, cRead(o, 1));
      cWrite(S, 0, cRead(S, 1));
      cWrite(B, 0, cRead(B, 1));
      cWrite(b, 0, cRead(b, 1));
      cWrite(c, 0, cRead(c, 1));
      cWrite(s, 0, cRead(s, 1));
      cWrite(i, 0, cRead(i, 1));
      cWrite(l, 0, cRead(l, 1));
      cWrite(f, 0, cRead(f, 1));
      cWrite(d, 0, cRead(d, 1));

      assertSame(o[0],o[1]);
      assertSame(S[0],S[1]);
      assertSame(B[0],B[1]);
      assertEquals(b[0],b[1]);
      assertEquals(c[0],c[1]);
      assertEquals(s[0],s[1]);
      assertEquals(i[0],i[1]);
      assertEquals(l[0],l[1]);
      assertEquals(f[0],f[1],1e-30f);
      assertEquals(d[0],d[1],1e-30);
    }
  };
  
  @Test
  public final void testArrayAccess() {
    CSystem.forkAndJoin(rArrayAccess);
  }
 
}
