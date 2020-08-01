package rip.hippo.mosey.transformer.impl.flow

import org.objectweb.asm.tree.{InsnList, InsnNode, JumpInsnNode, LabelNode, MethodInsnNode, TryCatchBlockNode}
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.configuration.Configuration
import rip.hippo.mosey.transformer.Transformer
import rip.hippo.mosey.util.MathUtil
import rip.hippo.mosey.util.asm.InstructionUtil
import org.objectweb.asm.Opcodes._

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 *
 * Adds random try catches around instructions.
 *
 * <p>Before</p>
 * <code>
 *     System.out.println("Hello World");
 * </code>
 *
 * <p>After</p>
 * <code>
 *     PrintStream printStream;
 *     try {
 *         printStream = System.out;
 *     } catch (Throwable t) {
 *         throw t;
 *     }
 *     String s;
 *     try {
 *         s = "Hello World";
 *     } catch (Throwable t) {
 *         throw t;
 *     }
 *     printStream.println(s);
 * </code>
 */
final class FakeTryCatchesTransformer(configuration: Configuration) extends Transformer {
  private val chance: Int = configuration.get("FakeTryCatches", "chance")

  override def transform(classWrapper: ClassWrapper): Unit = {
    classWrapper.methods.foreach(method => {
      var invokedSuper = false
      val superCount = method.getInstructions.toArray.count(_.getOpcode == INVOKESPECIAL)
      var currentSuper = 0
      method.getInstructions.toArray.foreach(instruction => {
        if (method.getName.equals("<init>") && !invokedSuper && instruction.isInstanceOf[MethodInsnNode] && instruction.asInstanceOf[MethodInsnNode].name.equals("<init>")) {
          currentSuper += 1
          if (currentSuper == superCount)
            invokedSuper = true
        } else {
          if ((!method.getName.equals("<init>") || invokedSuper) && MathUtil.chance(chance) && !InstructionUtil.isDefective(instruction)) {
            val start = new LabelNode
            val handler = new LabelNode
            val end = new LabelNode
            val catchBlock = new InsnList
            catchBlock.add(handler)
            catchBlock.add(new InsnNode(ATHROW))
            catchBlock.add(end)
            method.getInstructions.insertBefore(instruction, start)
            method.getInstructions.insert(instruction, catchBlock)
            method.getInstructions.insert(instruction, new JumpInsnNode(GOTO, end))
            method.getTryCatchBlocks.add(new TryCatchBlockNode(start, end, handler, "java/lang/Throwable"))
          }
        }
      })
    })
  }
}
