package org.cooperari.core.aspectj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.cooperari.core.util.IO;

/**
 * 
 * Wrapper class for invocation of the AspectJ compiler.
 * 
 * @since 0.2 
 *
 */
public final class Compiler {

  /**
   * @param classes Classes to compile.
   * @param options Compiler options.
   * @param abcInpFile Compiler input JAR file (this is generated internally).
   * @param abcOutFile Compiler output JAR file.
   * @param ajcMsgs List object to hold compiler messages.
   * @return <code>true</code> if compilation executed without errors,
   *         <code>false</code> otherwise.
   * @throws IOException if an I/O error occurs.
   */
  public static boolean compile(Collection<Class<?>> classes, String options,
      File abcInpFile, File abcOutFile, List<String> ajcMsgs)
      throws IOException {

    JarOutputStream jos = new JarOutputStream(new FileOutputStream(abcInpFile));

    // 1. Prepare input JAR file for the aspectJ compiler.
    try {
      jos = new JarOutputStream(new FileOutputStream(abcInpFile));
      for (Class<?> c : classes) {
        String entryName = c.getCanonicalName().replace('.', '/')
            .concat(".class");
        jos.putNextEntry(new JarEntry(entryName));
        InputStream is = c.getClassLoader().getResourceAsStream(entryName);
        IO.bCopy(is, jos);
        is.close();
        jos.closeEntry();
      }
    } finally {
      jos.close();
    }
    // 2. Call the AspectJ compiler
    String[] ajcArgs = String.format(
        "-8 -Xjoinpoints:synchronization -injars %s -outjar %s %s",
        abcInpFile.getAbsolutePath(), abcOutFile.getAbsolutePath(), options)
        .split(" ");

    ajcMsgs.clear();
    int nErrors = org.aspectj.tools.ajc.Main.bareMain(ajcArgs, false,
        ajcMsgs, ajcMsgs, ajcMsgs, ajcMsgs);
    return nErrors == 0;
  }

  /**
   * Private constructor to prevent instantiation.
   */
  private Compiler() {
    
  }
}
