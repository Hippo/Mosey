package rip.hippo.mosey.asm.wrapper

import java.util

import org.objectweb.asm.Attribute
import org.objectweb.asm.tree.{AnnotationNode, FieldNode}

/**
 * @author Hippo
 * @version 1.0.0, 7/8/20
 * @since 1.0.0
 */
final class FieldWrapper(fieldNode: FieldNode) {

  def addModifier(modifier: Int): Unit = fieldNode.access |= modifier

  def removeModifier(modifier: Int): Unit = fieldNode.access &= ~modifier

  def hasModifier(modifier: Int): Boolean = (fieldNode.access & modifier) != 0

  def addVisibleAnnotation(annotationNode: AnnotationNode): Unit = {
    if (fieldNode.visibleAnnotations == null) {
      fieldNode.visibleAnnotations = new util.ArrayList(1)
    }
    fieldNode.visibleAnnotations.add(annotationNode)
  }

  def getAttributes: util.List[Attribute] = fieldNode.attrs

  def createAttributes: Unit = fieldNode.attrs = new util.ArrayList[Attribute]()

}
