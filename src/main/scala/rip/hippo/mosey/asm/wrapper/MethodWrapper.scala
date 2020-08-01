package rip.hippo.mosey.asm.wrapper

import java.util

import org.objectweb.asm.Attribute
import org.objectweb.asm.tree.{AnnotationNode, InsnList, MethodNode, TryCatchBlockNode}

/**
 * @author Hippo
 * @version 1.0.0, 7/8/20
 * @since 1.0.0
 */
final class MethodWrapper(methodNode: MethodNode) {

  def addVisibleAnnotation(annotationNode: AnnotationNode): Unit = {
    if (methodNode.visibleAnnotations == null) {
      methodNode.visibleAnnotations = new util.ArrayList()
    }
    methodNode.visibleAnnotations.add(annotationNode)
  }

  def addModifier(modifier: Int): Unit = methodNode.access |= modifier

  def removeModifier(modifier: Int): Unit = methodNode.access &= ~modifier

  def hasModifier(modifier: Int): Boolean = (methodNode.access & modifier) != 0

  def getInstructions: InsnList = methodNode.instructions

  def getTryCatchBlocks: util.List[TryCatchBlockNode] = methodNode.tryCatchBlocks

  def getAttributes: util.List[Attribute] = methodNode.attrs

  def createAttributes: Unit = methodNode.attrs = new util.ArrayList[Attribute]()

  def getMaxLocals: Int = methodNode.maxLocals

  def getName: String = methodNode.name
}
