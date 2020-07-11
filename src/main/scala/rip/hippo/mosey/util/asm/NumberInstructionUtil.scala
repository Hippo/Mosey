package rip.hippo.mosey.util.asm

import org.objectweb.asm.Opcodes._
import org.objectweb.asm.tree.{AbstractInsnNode, InsnNode, IntInsnNode, LdcInsnNode}

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 */
object NumberInstructionUtil {


  def getOptimizedInt(value: Int): AbstractInsnNode = {
    value match {
      case x if x >= -1 && x <= 5 => new InsnNode(toConst(x))
      case x if x >= Byte.MinValue && x <= Byte.MaxValue => new IntInsnNode(BIPUSH, x)
      case x if x >= Short.MinValue && x <= Short.MaxValue => new IntInsnNode(SIPUSH, x)
      case _ => new LdcInsnNode(value)
    }
  }

  private def toConst(value: Int): Int = {
    val opcode = value + 3
    if (opcode >= ICONST_M1 && opcode <= ICONST_5) return opcode
    throw new IllegalArgumentException(String.format("Value %d can't be converted to a constant opcode.", value))
  }

  private def fromConst(opcode: Int): Int = {
    val value = opcode - 3
    if (value >= -1 && value <= 5) return value
    throw new IllegalArgumentException(String.format("Opcode %d can't be converted to integer value.", opcode))
  }

  def extractInteger(abstractInsnNode: AbstractInsnNode): Integer = {
    abstractInsnNode match {
      case insn if insn.getOpcode >= ICONST_M1 && abstractInsnNode.getOpcode <= ICONST_5 => fromConst(insn.getOpcode)
      case int: IntInsnNode => int.operand
      case ldc: LdcInsnNode if ldc.cst.isInstanceOf[Integer] => ldc.cst.asInstanceOf[Integer]
      case _ => null
    }
  }


}
