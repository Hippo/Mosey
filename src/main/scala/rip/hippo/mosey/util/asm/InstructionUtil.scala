package rip.hippo.mosey.util.asm

import org.objectweb.asm.Opcodes.NOP
import org.objectweb.asm.tree.{AbstractInsnNode, FrameNode, LabelNode, LineNumberNode}

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 */
object InstructionUtil {

  def isDefective(abstractInsnNode: AbstractInsnNode): Boolean = abstractInsnNode.isInstanceOf[LabelNode] || abstractInsnNode.isInstanceOf[LineNumberNode] || abstractInsnNode.isInstanceOf[FrameNode] || abstractInsnNode.getOpcode == NOP

}
