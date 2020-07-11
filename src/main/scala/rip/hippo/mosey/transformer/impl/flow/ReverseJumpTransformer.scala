package rip.hippo.mosey.transformer.impl.flow

import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.transformer.Transformer
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.tree.{InsnList, JumpInsnNode, LabelNode}
import rip.hippo.mosey.util.asm.JumpInstructionUtil

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 *
 * This transformer reverses jump opcodes which may or may not make it look weird on some decompilers.
 * It may make some if statements into ternary operations.
 *
 * <p>Before</p>
 * <code>
 *     ...
 *     IFNE l0
 *     ...
 *     l0
 * </code>
 *
 * <p>After</p>
 * <code>
 *     ...
 *     IFEQ l0
 *     GOTO l1 (previously l0)
 *     l0
 *     ...
 *     l1
 * </code>
 */
final class ReverseJumpTransformer extends Transformer {

  override def transform(classWrapper: ClassWrapper): Unit = {
    classWrapper.methods.foreach(method => {
      method.getInstructions.toArray.foreach(instruction => {
        val opcode = instruction.getOpcode
        if (opcode >= IFEQ && opcode <= IF_ACMPNE) {
          val jumpInsnNode = instruction.asInstanceOf[JumpInsnNode]
          val offset = new LabelNode
          val insnList = new InsnList
          insnList.add(new JumpInsnNode(GOTO, jumpInsnNode.label))
          insnList.add(offset)
          jumpInsnNode.setOpcode(JumpInstructionUtil.reverseJump(opcode))
          jumpInsnNode.label = offset
          method.getInstructions.insert(jumpInsnNode, insnList)
        }
      })
    })
  }
}
