package rip.hippo.mosey

import java.io.File

import rip.hippo.mosey.configuration.Configuration
import rip.hippo.mosey.jar.impl.{StandardJarExporter, StandardJarLoader}
import rip.hippo.mosey.jar.resource.ResourceManager
import rip.hippo.mosey.jar.resource.impl.ClassResource
import rip.hippo.mosey.jar.{JarExporter, JarLoader}
import rip.hippo.mosey.logger.Logger
import rip.hippo.mosey.transformer.TransformerManager

/**
 * @author Hippo
 * @version 1.0.0, 7/10/20
 * @since 1.0.0
 */
final class Mosey(configuration: Configuration, jarLoader: JarLoader = new StandardJarLoader, jarExporter: JarExporter = new StandardJarExporter) {
  val resourceManager = new ResourceManager
  val transformerManager = new TransformerManager(configuration, resourceManager)

  def loadRuntime: Unit = loadJar(configuration.getRuntime, true)
  def loadInput: Unit = loadJar(configuration.getInput, false)
  def loadJar(input: File, library: Boolean): Unit = jarLoader.loadJar(input, resourceManager , library, configuration.shouldInlineJSR)
  def transform: Unit = {
    Logger.info("Transforming jar...")
    resourceManager.resources.filter(resource => resource.isInstanceOf[ClassResource]).foreach(resource => transformerManager.transform(resource.asInstanceOf[ClassResource].classWrapper))
    Logger.info("Jar transformed")
  }
  def exportJar: Unit = jarExporter.exportJar(resourceManager, configuration.getOutput)
}
