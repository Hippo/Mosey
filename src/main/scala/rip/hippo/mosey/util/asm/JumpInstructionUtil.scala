package rip.hippo.mosey.util.asm

import org.objectweb.asm.Opcodes._

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 */
object JumpInstructionUtil {

  def reverseJump(opcode: Int): Int = opcode match {
    case IFNE => IFEQ
    case IFEQ => IFNE
    case IFGE => IFLT
    case IFGT => IFLE
    case IFLE => IFGT
    case IFLT => IFGE
    case IFNONNULL => IFNULL
    case IFNULL => IFNONNULL
    case IF_ACMPEQ => IF_ACMPNE
    case IF_ACMPNE => IF_ACMPEQ
    case IF_ICMPEQ => IF_ICMPNE
    case IF_ICMPNE => IF_ICMPEQ
    case IF_ICMPGE => IF_ICMPLT
    case IF_ICMPGT => IF_ICMPLE
    case IF_ICMPLE => IF_ICMPGT
    case IF_ICMPLT => IF_ICMPGE
    case _ => throw new IllegalStateException(String.format("Unable to reverse jump opcode: %d", opcode))
  }

}
