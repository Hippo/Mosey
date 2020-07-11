package rip.hippo.mosey.util.asm

import org.objectweb.asm.tree.{FieldInsnNode, InsnList, InsnNode, LdcInsnNode, MethodInsnNode}
import org.objectweb.asm.Opcodes._
import rip.hippo.mosey.util.MathUtil

import scala.util.Random

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 */
object ObfuscatedInstructionUtil {

  def generateTrashInstructions: InsnList = {
    val insnList = new InsnList
    Random.nextInt(2) match {
      case 0 =>
        insnList.add(new InsnNode(ICONST_0))
        insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "exit", "(I)V"))
      case 1 =>
        val out = MathUtil.randomBoolean
        insnList.add(new FieldInsnNode(GETSTATIC, "java/lang/System", if (out) "out" else "err", "Ljava/io/PrintStream;"))
        insnList.add(new LdcInsnNode(String.valueOf(System.currentTimeMillis)))
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"))

    }
    insnList
  }

}
