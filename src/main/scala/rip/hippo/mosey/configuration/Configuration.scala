package rip.hippo.mosey.configuration

import java.io.File

/**
 * @author Hippo
 * @version 2.0.0, 7/10/20
 * @since 1.0.0
 */
trait Configuration {
  def getInput: File
  def getOutput: File
  def getRuntime: File
  def shouldInlineJSR: Boolean
  def shouldLogLibraries: Boolean
  def getTransformers: List[String]
  def getLibraries: List[File]
  def getExcluded: List[String]
  def getDictionary: String
  def get[T](parent: String, key: String): T

}
