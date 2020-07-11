package rip.hippo.mosey.jar

import rip.hippo.mosey.jar.resource.ResourceManager

import java.io.File

/**
 * @author Hippo
 * @version 1.0.0, 7/8/20
 * @since 1.0.0
 */
trait JarExporter {
  def exportJar(resourceManager: ResourceManager, output: File): Unit
}
