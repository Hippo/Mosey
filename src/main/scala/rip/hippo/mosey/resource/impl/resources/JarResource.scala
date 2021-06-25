package rip.hippo.mosey.resource.impl.resources

import rip.hippo.mosey.resource.Resource

/**
 * @author Hippo
 * @version 2.0.0, 6/25/21
 * @since 2.0.0
 */
final case class JarResource(name: String, resourceBytes: Array[Byte]) extends Resource {
  override def getName: String =
    name

  override def toByteArray: Array[Byte] =
    resourceBytes
}
