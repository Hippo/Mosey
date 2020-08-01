package rip.hippo.mosey

import logger.Logger
import org.objectweb.asm.tree.JumpInsnNode
import rip.hippo.mosey.asm.ClassHierarchy
import rip.hippo.mosey.configuration.impl.JavaScriptConfiguration

object Main {



  def main(args: Array[String]): Unit = {
    try {
      val configPath = args.find(arg => arg.startsWith("-config")) match {
        case None => "Config.js"
        case Some(value) => value.substring("-config".length)
      }

      val configuration = new JavaScriptConfiguration(configPath)
      val mosey = new Mosey(configuration)

      mosey.loadRuntime
      configuration.getLibraries.foreach(file => mosey.loadJar(file, true))
      mosey.loadInput
      ClassHierarchy.registerSuperclasses

      mosey.transform

      mosey.exportJar
    } catch {
      case e: Exception => Logger.error(e, "An error has occurred in Mosey, please report to hippo.")
    }
  }
}