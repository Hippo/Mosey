package rip.hippo.mosey.util

import java.io.ByteArrayOutputStream
import scala.annotation.tailrec
import java.util.jar.{JarEntry, JarFile}

/**
 * @author Hippo
 * @version 1.0.0, 7/10/20
 * @since 1.0.0
 */
object IOUtil {

  def toByteArray(jarFile: JarFile, jarEntry: JarEntry): Array[Byte] = {
    val inputStream = jarFile.getInputStream(jarEntry)
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val length = (jarEntry.getSize / 8).asInstanceOf[Int]
    val buffer = new Array[Byte](length)
    @tailrec def read(): Unit = {
      val len = inputStream.read(buffer)
      if (len > 0) {
        byteArrayOutputStream.write(buffer, 0, len)
        read()
      }
    }
    read()
    byteArrayOutputStream.toByteArray
  }
}
