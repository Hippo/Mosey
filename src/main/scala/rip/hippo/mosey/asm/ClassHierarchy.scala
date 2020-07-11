package rip.hippo.mosey.asm
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.logger.Logger

import scala.collection.mutable

/**
 * @author Hippo
 * @version 1.0.0, 7/8/20
 * @since 1.0.0
 */
object ClassHierarchy {
  private val classLookup: mutable.Map[String, ClassWrapper] = mutable.Map()
  private val classHierarchy: mutable.Map[ClassWrapper, ClassWrapper] = mutable.Map()

  def registerClass(classWrapper: ClassWrapper): Unit =
    classLookup += (classWrapper.getName -> classWrapper)

  def lookup(name: String): Option[ClassWrapper] =
    classLookup.get(name)

  def registerSuperclasses: Unit =
    classLookup.values.foreach(classWrapper => setSuperClass(classWrapper, classWrapper.getSuperName))

  def setSuperClass(classWrapper: ClassWrapper, name: String): Unit =
    lookup(name) match {
      case None => Logger.warn(String.format("Class %s can't be found, ensure all libraries are loaded, ignoring.", name).toString)
      case Some(value) => classHierarchy += (classWrapper -> value)
    }

  def getSuperClass(classWrapper: ClassWrapper): ClassWrapper =
    classHierarchy(classWrapper)
}
