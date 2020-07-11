package rip.hippo.mosey.asm.wrapper

import java.util

import org.objectweb.asm.tree.{AnnotationNode, ClassNode, FieldNode}

import scala.collection.mutable.ListBuffer

/**
 * @author Hippo
 * @version 1.0.0, 7/8/20
 * @since 1.0.0
 */
final class ClassWrapper(classNode: ClassNode) {

  val methods: ListBuffer[MethodWrapper] = ListBuffer()
  classNode.methods.stream().map(methodNode => new MethodWrapper(methodNode)).forEach(methodWrapper => methods += methodWrapper)

  val fields: ListBuffer[FieldWrapper] = ListBuffer()
  classNode.fields.stream().map(fieldNode => new FieldWrapper(fieldNode)).forEach(fieldWrapper => fields += fieldWrapper)

  def addVisibleAnnotation(annotationNode: AnnotationNode): Unit = {
    if (classNode.visibleAnnotations == null) {
      classNode.visibleAnnotations = new util.ArrayList(1)
    }
    classNode.visibleAnnotations.add(annotationNode)
  }

  def hasModifier(modifier: Int): Boolean = (classNode.access & modifier) != 0

  def getName: String = classNode.name

  def getSuperName: String = classNode.superName

  def getClassNode: ClassNode = classNode

}
