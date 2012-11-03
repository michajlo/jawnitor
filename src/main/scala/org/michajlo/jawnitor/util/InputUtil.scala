package org.michajlo.jawnitor.util

import java.io.Reader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader

object InputUtil {

  /**
   * Create a reader for a specific location. First the filesystem
   * will be checked, then the classpath.
   *
   * @param location where to look for the file, as a relative or
   *        absolute path
   *
   * @return Some(Reader) if something was found, None if not
   */
  def getReaderFor(location: String): Option[Reader] = {
    val file = new File(location)
    if (file.exists()) {
      return Some(new FileReader(file))
    } else {
      val cpResourceStream = getClass.getResourceAsStream(location)
      if (cpResourceStream != null) {
        Some(new InputStreamReader(cpResourceStream))
      } else {
        None
      }
    }
  }

}