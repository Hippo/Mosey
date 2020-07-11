package rip.hippo.mosey.jar.resource

/**
 * @author Hippo
 * @version 1.0.0, 7/8/20
 * @since 1.0.0
 */
trait Resource {
  def getName: String
  def toByteArray: Array[Byte]
}