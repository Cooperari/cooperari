package org.cooperari.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.cooperari.errors.CInternalError;
/**
 * Class with utility I/O methods.
 *
 * @since 0.2
 */
public final class IO {

  /**
   * Default buffer size in bytes for buffered operations.
   */
  public static final int DEFAULT_BUFFER_SIZE = 16384;
  
  /**
   * Copy data from an input stream onto an output stream using a buffer
   * of length {@link #DEFAULT_BUFFER_SIZE} bytes.
   * 
   * @param is Input stream.
   * @param os Output stream.
   * @return The number of bytes copied between streams.
   * @throws IOException If an I/O error occurs.
   * @see #bCopy(InputStream, OutputStream, int)
   */
  public static int bCopy(InputStream is, OutputStream os) throws IOException {
    return bCopy(is, os, DEFAULT_BUFFER_SIZE);
  }
  
  /**
   * Copy data from an input stream onto an output stream using buffering,
   * using a custom size for the buffer length.
   * 
   * @param is Input stream.
   * @param os Output stream.
   * @param bufferSize Buffer size.
   * @throws IOException If an I/O error occurs.
   * @return The number of bytes copied between streams.
   */
  public static int bCopy(InputStream is, OutputStream os, int bufferSize) throws IOException {
    byte[] buffer = new byte[bufferSize];
    int count = 0;
    do {
      int n = is.read(buffer, 0, Math.min(buffer.length, is.available()));
      count += n;
      os.write(buffer, 0, n);
    } while(is.available() > 0);
    return count;
  }

  /**
   * Get canonical path for file.
   * 
   * It masks a call to {@link java.io.File#getCanonicalPath()}
   * and wraps any {@link java.io.IOException} with {@link org.cooperari.errors.CInternalError}.
   
   * @param file File.
   * @return Canonical path for file.
   * @throws CInternalError if a  {@link java.io.IOException} occurs.
   */
  public static String fullPath(File file) {
    try {
      return file.getCanonicalPath();
    } catch (IOException e) {
      throw new CInternalError(e);
    }
  }
  /**
   * Private constructor to avoid instantiation.
   */
  private IO() { }
    
}
