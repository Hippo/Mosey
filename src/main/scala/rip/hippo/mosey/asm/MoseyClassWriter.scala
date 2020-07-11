package rip.hippo.mosey.asm

import scala.collection.mutable
import scala.annotation.tailrec

import org.objectweb.asm.ClassWriter
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import org.objectweb.asm.Opcodes._


/**
 * @author Hippo
 * @version 1.0.0, 7/8/20
 * @since 1.0.0
 */
final class MoseyClassWriter(flags: Int) extends ClassWriter(flags) {

  override def getCommonSuperClass(type1: String, type2: String): String = {
    try {
      super.getCommonSuperClass(type1, type2)
    } catch  {
      case _: Exception  =>
        if (type1.equals(type2)) return type1
        if (type1.equals("java/lang/Object") || type2.equals("java/lang/Object")) return "java/lang/Object"


        val lookupType1 = ClassHierarchy.lookup(type1) match {
          case None => ClassHierarchy.lookup("java/lang/Object").get
          case Some(value) => value
        }
        val lookupType2 = ClassHierarchy.lookup(type2) match {
          case None => ClassHierarchy.lookup("java/lang/Object").get
          case Some(value) => value
        }

        if (lookupType1.hasModifier(ACC_INTERFACE) || lookupType2.hasModifier(ACC_INTERFACE)) return "java/lang/Object"

        val lookupType1Super = ClassHierarchy.getSuperClass(lookupType1)
        val lookupType2Super = ClassHierarchy.getSuperClass(lookupType2)

        if (lookupType1Super.getName.equals("java/lang/Object") || lookupType2Super.getName.equals("java/lang/Object")) return "java/lang/Object"
        if (lookupType2Super.getName.equals(lookupType1.getName)) return lookupType1.getName
        if (lookupType1Super.getName.equals(lookupType2.getName)) return lookupType2.getName
        if (lookupType1Super.getName.equals(lookupType2Super.getName)) return lookupType1Super.getName

        val treeType1 = tree(lookupType1Super)
        val treeType2 = tree(lookupType2Super)

        def find(shortest: mutable.Set[String], longest: mutable.Set[String]): Option[String] = {
          shortest.find(`type` => longest.contains(`type`))
        }

        val firstLongest = treeType1.size > treeType2.size
        find(if (firstLongest) treeType2 else treeType1, if (firstLongest) treeType1 else treeType2) match {
          case None =>
          case Some(value) => return value
        }

        "java/lang/Object"
    }
  }

  private def tree(classWrapper: ClassWrapper): mutable.Set[String] = {
    val tree: mutable.Set[String] = mutable.Set()

    @tailrec def search(parent: ClassWrapper): Unit = {
      if (!parent.getName.equals("java/lang/Object")) {
        tree.add(parent.getName)
        search(ClassHierarchy.getSuperClass(parent))
      }
    }

    search(classWrapper)
    tree
  }
}

object MoseyClassWriter {
  def apply(): MoseyClassWriter = new MoseyClassWriter(ClassWriter.COMPUTE_FRAMES)
  def apply(flags: Int) = new MoseyClassWriter(flags)
}
