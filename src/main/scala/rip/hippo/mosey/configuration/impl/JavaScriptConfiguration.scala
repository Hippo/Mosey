package rip.hippo.mosey.configuration.impl

import java.io.{File, FileReader, IOException}

import javax.script.{ScriptEngineManager, ScriptException}
import jdk.nashorn.api.scripting.ScriptObjectMirror
import rip.hippo.mosey.configuration.Configuration
import rip.hippo.mosey.logger.Logger

import scala.collection.mutable.ListBuffer

/**
 * @author Hippo
 * @version 1.0.0, 7/10/20
 * @since 1.0.0
 */
final class JavaScriptConfiguration(configPath: String) extends Configuration {

  private val scriptEngineManager = new ScriptEngineManager()
  private val scriptEngine = scriptEngineManager.getEngineByExtension("js")
  private var fileReader: FileReader = _

  try {
    fileReader = new FileReader(new File(configPath))
    Logger.info("Evaluating config...")
    scriptEngine.eval(fileReader)
    Logger.info("Config evaluated.")
  } catch {
    case e@(_: IOException | _: ScriptException) => Logger.error(e, String.format("Failed to evaluate config %s.", configPath))
  } finally if(fileReader != null) fileReader.close()


  private var runtimePath = scriptEngine.get("runtime").toString

  if (runtimePath.equals("detect")) {
    Logger.info("Trying to find Java Runtime (rt.jar)...")
    Option(System.getenv("JAVA_HOME")) match {
      case None => throw new RuntimeException("JAVA_HOME is not set.")
      case Some(value) => runtimePath = value + "/jre/lib/rt.jar"
    }
  }

  val runtime = new File(runtimePath)
  Logger.info(if (runtime.exists()) "Runtime found." else "Runtime missing.")

  val input = new File(scriptEngine.get("input").toString)
  val output = new File(scriptEngine.get("output").toString)
  val inlineJSR: Boolean = scriptEngine.get("inlineJSR").asInstanceOf[Boolean]
  val logLibraries: Boolean = scriptEngine.get("logLibraries").asInstanceOf[Boolean]
  val transformers: ListBuffer[String] = ListBuffer()
  scriptEngine.get("transformers").asInstanceOf[ScriptObjectMirror].values().stream().map(ref => ref.toString).forEach(string => transformers += string)
  val libraries: ListBuffer[File] = ListBuffer()
  scriptEngine.get("libraries").asInstanceOf[ScriptObjectMirror].values().stream().map(ref => new File(ref.toString)).forEach(file => libraries += file)
  val dictionary: String = scriptEngine.get("dictionary").toString

  override def getInput: File = input

  override def getOutput: File = output

  override def getRuntime: File = runtime

  override def shouldInlineJSR: Boolean = inlineJSR

  override def shouldLogLibraries: Boolean = logLibraries;

  override def getTransformers: List[String] = transformers.result()

  override def getLibraries: List[File] = libraries.result()

  override def getDictionary: String = dictionary

  override def get[T](parent: String, key: String): T = {
    scriptEngine.get(parent).asInstanceOf[ScriptObjectMirror].get(key).asInstanceOf[T]
  }
}