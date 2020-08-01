package rip.hippo.mosey.util.asm

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.tree.{InsnList, InsnNode, MethodNode}
import rip.hippo.mosey.asm.wrapper.{ClassWrapper, MethodWrapper}

/**
 * @author Hippo
 * @version 1.0.0, 7/31/20
 * @since 1.0.0
 */
object MethodUtil {

  def getClinit(classWrapper: ClassWrapper): MethodWrapper = {
    classWrapper.methods.find(methodWrapper => methodWrapper.getName.equals("<clinit>")) match {
      case Some(value) => value
      case None =>
        val clinitMethodNode = new MethodNode(ACC_STATIC, "<clinit>", "()V", null, null)
        clinitMethodNode.instructions = new InsnList
        clinitMethodNode.instructions.add(new InsnNode(Opcodes.RETURN))
        classWrapper.addMethod(clinitMethodNode)
    }
  }

}
