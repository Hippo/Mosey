package rip.hippo.mosey.jar.impl

import java.io.{File, IOException}
import java.util.jar.JarFile

import rip.hippo.mosey.asm.ClassHierarchy
import rip.hippo.mosey.configuration.Configuration
import rip.hippo.mosey.jar.JarLoader
import rip.hippo.mosey.jar.resource.ResourceManager
import rip.hippo.mosey.jar.resource.impl.{ClassResource, JarResource}
import rip.hippo.mosey.logger.Logger
import rip.hippo.mosey.util.IOUtil


/**
 * @author Hippo
 * @version 1.1.0, 7/9/20
 * @since 1.0.0
 */
final class StandardJarLoader extends JarLoader {

  override def loadJar(input: File, resourceManager: ResourceManager, library: Boolean, configuration: Configuration): Unit = {
    val log = !library || (library && configuration.shouldLogLibraries)
    if (log) Logger.info(String.format("Loading %s " + (if (library) "(library) " else ""), input.getAbsolutePath))

    val exclude: String => Boolean = x => configuration.getExcluded.exists(exclude => x.startsWith(exclude))

    try {
      val jarFile = new JarFile(input)
      val entries = jarFile.entries()
      while (entries.hasMoreElements) {
        val jarEntry = entries.nextElement()
        val bytes = IOUtil.toByteArray(jarFile, jarEntry)
        val classFile = jarEntry.getName.contains(".class")
        if (log) Logger.info(String.format("Loading resource %s", jarEntry.getName))
        if (library) {
          if (classFile) {
            registerHierarchy(ClassResource(bytes, true, configuration.shouldInlineJSR))
          }
        } else {
          resourceManager.addResource(if (classFile && !exclude(jarEntry.getName)) registerHierarchy(ClassResource(bytes, false, configuration.shouldInlineJSR)) else JarResource(jarEntry.getName, bytes))
        }
      }
      jarFile.close()
    } catch {
      case e: IOException => Logger.error(e, String.format("Failed to read jar file %s", input.getAbsolutePath))
    }
    if (log) Logger.info("Resources loaded.\n")
  }

  def registerHierarchy(classResource: ClassResource): ClassResource = {
    val classWrapper = classResource.classWrapper
    ClassHierarchy.registerClass(classWrapper)
    classResource
  }
}
