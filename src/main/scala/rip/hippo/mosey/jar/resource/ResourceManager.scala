package rip.hippo.mosey.jar.resource

import scala.collection.mutable.ListBuffer

/**
 * @author Hippo
 * @version 1.0.0, 7/8/20
 * @since 1.0.0
 */
final class ResourceManager {
  val resources: ListBuffer[Resource] = ListBuffer()

  def addResource(resource: Resource): Unit = {
    resources += resource
  }
}
