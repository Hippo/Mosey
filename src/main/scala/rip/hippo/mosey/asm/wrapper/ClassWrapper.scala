package rip.hippo.mosey.asm.wrapper

import java.util

import org.objectweb.asm.Attribute
import org.objectweb.asm.tree.{AnnotationNode, ClassNode, FieldNode, MethodNode}

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

  var archiveName = classNode.name + ".class"

  def addVisibleAnnotation(annotationNode: AnnotationNode): Unit = {
    if (classNode.visibleAnnotations == null) {
      classNode.visibleAnnotations = new util.ArrayList(1)
    }
    classNode.visibleAnnotations.add(annotationNode)
  }

  def addMethod(methodNode: MethodNode): MethodWrapper = {
    val wrapped = new MethodWrapper(methodNode)
    classNode.methods.add(0, methodNode)
    methods += wrapped
    wrapped
  }

  def addField(fieldNode: FieldNode): FieldWrapper = {
    val wrapped = new FieldWrapper(fieldNode)
    classNode.fields.add(0, fieldNode)
    fields += wrapped
    wrapped
  }

  def hasModifier(modifier: Int): Boolean = (classNode.access & modifier) != 0

  def getAttributes: util.List[Attribute] = classNode.attrs

  def createAttributes: Unit = classNode.attrs = new util.ArrayList[Attribute]()

  def getName: String = classNode.name

  def getSuperName: String = classNode.superName

  def getClassNode: ClassNode = classNode

}
