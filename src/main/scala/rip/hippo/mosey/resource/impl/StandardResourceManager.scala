package rip.hippo.mosey.resource.impl

import rip.hippo.mosey.resource.{Resource, ResourceManager}

import scala.collection.mutable

/**
 * @author Hippo
 * @version 2.0.0, 6/25/21
 * @since 2.0.0
 */
final case class StandardResourceManager() extends ResourceManager {

  private val resourceMap: mutable.Map[String, Resource] = mutable.Map[String, Resource]()

  override def add(resource: Resource): Unit =
    resourceMap += (resource.getName -> resource)

  override def lookup(name: String): Option[Resource] =
    resourceMap.get(name)
}

