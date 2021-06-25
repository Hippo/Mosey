package rip.hippo.mosey.resource

/**
 * @author Hippo
 * @version 2.0.0, 6/25/21
 * @since 2.0.0
 */
trait ResourceManager {
  def add(resource: Resource): Unit
  def lookup(name: String): Option[Resource]
}
