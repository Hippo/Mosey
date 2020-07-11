package rip.hippo.mosey.transformer.impl.flow

import org.objectweb.asm.Opcodes._
import org.objectweb.asm.tree.{InsnList, InsnNode, JumpInsnNode, LabelNode, LookupSwitchInsnNode, VarInsnNode}
import rip.hippo.mosey.analyze.StackSizeAnalyzer
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.configuration.Configuration
import rip.hippo.mosey.transformer.Transformer
import rip.hippo.mosey.util.MathUtil
import rip.hippo.mosey.util.asm.NumberInstructionUtil

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 *
 * Damn, even after a rewrite I still didn't decide to finish this
 */
final class ConfusingSwitchTransformer(configuration: Configuration) extends Transformer {
  val constants: Boolean = configuration.get("ConfusingSwitch", "constants")
  val chance: Int = configuration.get("ConfusingSwitch", "chance")

  override def transform(classWrapper: ClassWrapper): Unit = {
    classWrapper.methods
      .filter(method => !method.getName.equals("<init>"))
      .foreach(method => {
        val stack = StackSizeAnalyzer.emulateStack(method)
        val first = MathUtil.generate
        val second = MathUtil.generate
        val firstIndex = method.getMaxLocals
        val secondIndex = firstIndex + 1
        val free = secondIndex + 1

        val setTrash = new InsnList
        setTrash.add(NumberInstructionUtil.getOptimizedInt(first))
        setTrash.add(new VarInsnNode(ISTORE, firstIndex))
        setTrash.add(NumberInstructionUtil.getOptimizedInt(second))
        setTrash.add(new VarInsnNode(ISTORE, secondIndex))

        method.getInstructions.toArray.foreach(instruction => {
          Option(NumberInstructionUtil.extractInteger(instruction)) match {
            case None =>
            case Some(value) if stack.get(instruction) == 0 && constants && MathUtil.chance(chance) =>
              val trapSwitch = new InsnList
              val real = new LabelNode
              val fake = new LabelNode
              val dflt = new LabelNode
              val realValue = first ^ second
              val offset = MathUtil.generate(1, realValue / 2)
              val fakeValue = realValue + (if (MathUtil.randomBoolean) -offset
              else offset)
              val realFirst = MathUtil.randomBoolean

              trapSwitch.add(new VarInsnNode(ILOAD, firstIndex))
              trapSwitch.add(new VarInsnNode(ILOAD, secondIndex))
              trapSwitch.add(new InsnNode(IXOR))
              trapSwitch.add(new VarInsnNode(ISTORE, free))
              trapSwitch.add(new VarInsnNode(ILOAD, free))
              trapSwitch.add(new LookupSwitchInsnNode(dflt,
                Array[Int](if (realFirst) realValue else fakeValue, if (realFirst) fakeValue else realValue),
                Array[LabelNode](if (realFirst) real else fake, if (realFirst) fake else real)))

              val realHandler = new InsnList
              realHandler.add(real)
              realHandler.add(NumberInstructionUtil.getOptimizedInt(value))
              realHandler.add(new VarInsnNode(ISTORE, free))
              realHandler.add(new JumpInsnNode(GOTO, dflt))

              val fakeHandler = new InsnList
              fakeHandler.add(fake)
              var extractedAbs = Math.abs(value)
              if (extractedAbs == 0) extractedAbs = MathUtil.generate(8, 32)
              val half = extractedAbs / 2
              val trash = MathUtil.generate(half, extractedAbs + half)
              fakeHandler.add(NumberInstructionUtil.getOptimizedInt(if (MathUtil.randomBoolean) trash else -trash))
              fakeHandler.add(new VarInsnNode(ISTORE, free))
              fakeHandler.add(new JumpInsnNode(GOTO, dflt))

              if (realFirst) {
                trapSwitch.add(realHandler)
                trapSwitch.add(fakeHandler)
              }
              else {
                trapSwitch.add(fakeHandler)
                trapSwitch.add(realHandler)
              }
              trapSwitch.add(dflt)
              trapSwitch.add(new VarInsnNode(ILOAD, free))

              method.getInstructions.insert(instruction, trapSwitch)
              method.getInstructions.remove(instruction)
            case _ =>
          }
        })
      })
  }
}
