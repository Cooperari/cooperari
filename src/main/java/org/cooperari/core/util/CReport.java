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

package org.cooperari.core.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Report file. 
 *
 * @since 0.2 
 */
public class CReport {

  /**
   * Output file.
   */
  private final File _file;
  
  /**
   * Output stream. 
   */
  private final PrintStream _out;
  
  /**
   * Create a report using a given file for output.
   * @param file Output file.
   * @throws IOException If an I/O error occurs.
   */
  public CReport(File file) throws IOException {
    _file = file;
    _out = new PrintStream(file);
  }

  /**
   * Get file associated to this report.
   * @return A {@link File} instance.
   */
  public File getFile() {
    return _file;
  }
  
  /**
   * Begin a section in the report.
   * @param title Title for the section.
   * @param columnHeaders Column headers.
   */
  public void beginSection(String title, Object... columnHeaders) {
    _out.printf("# %s%n", title);   
    if (columnHeaders != null && columnHeaders.length != 0) {
      write(columnHeaders);
    }
  }
  
  /**
   * Write an entry to the report.
   * @param values Entry values.
   */
  public void writeEntry(Object... values) {
    write(values);
  }
  
  /**
   * Flush the underlying output stream.
   */
  public void flush() {
    _out.flush();
  }
  
  /**
   * Close the report and underlying output stream.
   * The report should not be used after a call to this method.
   */
  public void close() {
    _out.close();
  }
  
  /**
   * Write a line to the report.
   * @param row Row of values. 
   */
  private void write(Object[] row) {
    StringBuilder sb = new StringBuilder();
    sb.append(row[0]);
    for (int i = 1; i < row.length ; i++ ) { 
      sb.append('\t').append(row[i]);
    }
    _out.println(sb.toString());
  }

  /**
   * Dump stack trace of an exception to report.
   * @param throwable The exception.
   */
  public void dumpStackTrace(Throwable throwable) {
    throwable.printStackTrace(_out);    
  }

}
