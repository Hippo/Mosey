package rip.hippo.mosey.jar

import java.io.File

import rip.hippo.mosey.jar.resource.ResourceManager

/**
 * @author Hippo
 * @version 1.0.0, 7/8/20
 * @since 1.0.0
 */
trait JarLoader {
  def loadJar(input: File, resourceManager: ResourceManager, library: Boolean, inlineJSR: Boolean): Unit
}
