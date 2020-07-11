package rip.hippo.mosey.transformer.impl.flow

import org.objectweb.asm.tree.{InsnList, InsnNode, JumpInsnNode, LabelNode, LdcInsnNode, MethodInsnNode}
import rip.hippo.mosey.analyze.StackSizeAnalyzer
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.configuration.Configuration
import rip.hippo.mosey.dictionary.Dictionary
import rip.hippo.mosey.transformer.Transformer
import rip.hippo.mosey.util.MathUtil
import org.objectweb.asm.Opcodes._
import rip.hippo.mosey.util.asm.{NumberInstructionUtil, ObfuscatedInstructionUtil}

import scala.util.Random

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 *
 * <p>Before</p>
 * <code>
 *     if (condition) {
 *         System.out.println("hello world");
 *     }
 * </code>
 *
 * <p>After</p>
 * <code>
 *     if(null == null) {
 *         if (condition) {
 *             if (!"pog".equals("poggers")) {
 *                 System.out.println("hello world");
 *             }
 *         }
 *     } else {
 *         throw null;
 *     }
 * </code>
 */
final class FakeJumpTransformer(configuration: Configuration, dictionary: Dictionary) extends Transformer {

  private val change: Int = configuration.get("FakeJump", "chance")

  override def transform(classWrapper: ClassWrapper): Unit = {
    classWrapper.methods.foreach(method => {
      val stack = StackSizeAnalyzer.emulateStack(method)
      method.getInstructions.toArray
        .filter(instruction => stack(instruction) == 0 && MathUtil.chance(change))
        .foreach(instruction => {
          val labelNode = new LabelNode
          val insnList = new InsnList
          val random = Random.nextInt(4)
          val follow = Random.nextBoolean()
          random match {
            case 0 =>
              insnList.add(new InsnNode(ACONST_NULL))
              insnList.add(new JumpInsnNode(if (follow) IFNULL else IFNONNULL, labelNode))
            case 1 =>
              insnList.add(new InsnNode(ICONST_0))
              insnList.add(new JumpInsnNode(if (follow) IFEQ else IFNE, labelNode))
            case 2 =>
              insnList.add(NumberInstructionUtil.getOptimizedInt(MathUtil.generate(1, Int.MaxValue)))
              insnList.add(new JumpInsnNode(if (follow) IFNE else IFEQ, labelNode))
            case 3 =>
              val first = dictionary.generate(MathUtil.generate(8, 16))
              insnList.add(new LdcInsnNode(first))
              var second = dictionary.generate(MathUtil.generate(8, 16))
              while (second.equals(first)) {
                second = dictionary.generate(MathUtil.generate(8, 16))
              }
              insnList.add(new LdcInsnNode(second))
              insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z"))
              insnList.add(new JumpInsnNode(if (follow) IFEQ else IFNE, labelNode))
            case _ =>
          }

          method.getInstructions.insertBefore(instruction, insnList)

          if (follow) {
            method.getInstructions.insertBefore(instruction, ObfuscatedInstructionUtil.generateTrashInstructions)
            method.getInstructions.insertBefore(instruction, labelNode)
          } else {
            method.getInstructions.add(labelNode)
            method.getInstructions.add(new InsnNode(ACONST_NULL))
            method.getInstructions.add(new InsnNode(ATHROW))
          }
        })
    })
  }
}
