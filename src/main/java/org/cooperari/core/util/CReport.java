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
   * Output stream. 
   */
  private final PrintStream _out;
  
  /**
   * Constructs a report.
   * @param out Output stream.
   */
  public CReport(PrintStream out) {
    _out = out;
  }
  
  /**
   * Create a report using a given file for output.
   * @param file Output file.
   * @throws IOException If an I/O error occurs.
   */
  public CReport(File file) throws IOException {
    this(new PrintStream(file));
  }

  /**
   * Begin a section in the report.
   * @param title Title for the section.
   * @param columnHeaders Column headers.
   */
  public void beginSection(String title, Object... columnHeaders) {
    _out.println("--- " + title +  "---") ;    
    write(columnHeaders);
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

}
