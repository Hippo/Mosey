package rip.hippo.mosey.resource

/**
 * @author Hippo
 * @version 2.0.0, 6/25/21
 * @since 2.0.0
 */
trait Resource {
  def getName: String
  def toByteArray: Array[Byte]
}
