package rip.hippo.mosey.jar.impl

import java.io.{File, FileOutputStream, IOException}
import java.util.jar.{JarEntry, JarOutputStream}

import rip.hippo.mosey.jar.JarExporter
import rip.hippo.mosey.jar.resource.ResourceManager
import rip.hippo.mosey.logger.Logger

/**
 * @author Hippo
 * @version 1.0.0, 7/10/20
 * @since 1.0.0
 */
final class StandardJarExporter extends JarExporter {

  override def exportJar(resourceManager: ResourceManager, output: File): Unit = {
    Logger.info("Attempting to export jar.")
    val jarOutputStream = new JarOutputStream(new FileOutputStream(output))
    try {
      resourceManager.resources.foreach(resource => {
        val jarEntry = new JarEntry(resource.getName)
        jarEntry.setCompressedSize(-1)
        jarOutputStream.putNextEntry(jarEntry)
        jarOutputStream.write(resource.toByteArray)
        jarOutputStream.closeEntry()
      })
    } catch {
      case e: IOException => Logger.error(e, String.format("Failed to write output file to %s", output.getAbsoluteFile))
    } finally {
      jarOutputStream.close()
    }
  }
}
