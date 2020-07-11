package rip.hippo.mosey.jar.resource.impl

import rip.hippo.mosey.jar.resource.Resource

/**
 * @author Hippo
 * @version 1.0.0, 7/8/20
 * @since 1.0.0
 */
final case class JarResource(name: String, resourceBytes: Array[Byte]) extends Resource() {
  override def getName: String =
    name

  override def toByteArray: Array[Byte] =
    resourceBytes
}