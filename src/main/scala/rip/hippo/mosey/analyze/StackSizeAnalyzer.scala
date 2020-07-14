package rip.hippo.mosey.analyze

import java.util
import java.util.stream.Collectors

import scala.collection.mutable
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.Type
import org.objectweb.asm.tree.{AbstractInsnNode, FieldInsnNode, InvokeDynamicInsnNode, LabelNode, LdcInsnNode, MethodInsnNode, MultiANewArrayInsnNode, TryCatchBlockNode}
import rip.hippo.mosey.asm.wrapper.MethodWrapper
import rip.hippo.mosey.logger.Logger

import scala.collection.mutable.ListBuffer

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 */
object StackSizeAnalyzer {

  def emulateStack(methodWrapper: MethodWrapper): mutable.Map[AbstractInsnNode, Integer] = {
    val instructionStackMap = mutable.Map[AbstractInsnNode, Integer]()
    val handlers = new ListBuffer[LabelNode]
    methodWrapper.getTryCatchBlocks.toArray().foreach(tcb => handlers += tcb.asInstanceOf[TryCatchBlockNode].handler)

    var stack = 0
    methodWrapper.getInstructions.toArray.foreach(instruction => {
      if (instruction.isInstanceOf[LabelNode] && handlers.contains(instruction)) stack = 1
      instructionStackMap += (instruction -> stack)
      instruction.getOpcode match {
        case ACONST_NULL =>
        case ICONST_M1 =>
        case ICONST_0 =>
        case ICONST_1 =>
        case ICONST_2 =>
        case ICONST_3 =>
        case ICONST_4 =>
        case ICONST_5 =>
        case FCONST_0 =>
        case FCONST_1 =>
        case FCONST_2 =>
        case BIPUSH =>
        case SIPUSH =>
        case LDC =>
        case ILOAD =>
        case FLOAD =>
        case ALOAD =>
        case DUP =>
        case DUP_X1 =>
        case DUP_X2 =>
        case I2L =>
        case I2D =>
        case F2L =>
        case F2D =>
        case NEW =>
        case JSR =>
          instruction match {
            case ldcInsnNode: LdcInsnNode if ldcInsnNode.cst.isInstanceOf[Double] || ldcInsnNode.cst.isInstanceOf[Long] => stack += 1
            case _ if instruction.getOpcode == JSR => Logger.warn("Found JSR instruction while analyzing stack, this may produce undefined behavior.")
            case _ =>
          }
          stack += 1

        case LCONST_0 =>
        case LCONST_1 =>
        case DCONST_0 =>
        case DCONST_1 =>
        case LLOAD =>
        case DLOAD =>
        case DUP2 =>
        case DUP2_X1 =>
        case DUP2_X2 =>
          stack += 2

        // (ref, index) -> (value)
        case IALOAD =>
        case FALOAD =>
        case AALOAD =>
        case BALOAD =>
        case CALOAD =>
        case SALOAD =>
        case ISTORE =>
        case FSTORE =>
        case POP =>
        case IADD =>
        case FADD =>
        case ISUB =>
        case FSUB =>
        case IMUL =>
        case FMUL =>
        case IDIV =>
        case FDIV =>
        case IREM =>
        case FREM =>
        case ISHL =>
        case ISHR =>
        case IUSHR =>
        // (long, int) -> (result)
        case LSHL =>
        case LSHR =>
        case LUSHR =>
        case IAND =>
        case IOR =>
        case IXOR =>
        case L2I =>
        case L2F =>
        case D2I =>
        case D2F =>
        // (float, float) -> (int)
        case FCMPL =>
        case FCMPG =>
        case IFEQ =>
        case IFNE =>
        case IFLT =>
        case IFGE =>
        case IFGT =>
        case IFLE =>
        case IFNULL =>
        case IFNONNULL =>
        // (index) -> ()
        case TABLESWITCH =>
        // (key) -> ()
        case LOOKUPSWITCH =>
        case IRETURN =>
        case FRETURN =>
        case ARETURN =>
        case ATHROW =>
        // (ref) -> ()
        case MONITORENTER =>
        case MONITOREXIT =>
          stack -= 1

        case LSTORE =>
        case DSTORE =>
        case POP2 =>
        case LADD =>
        case DADD =>
        case LSUB =>
        case DSUB =>
        case LMUL =>
        case DMUL =>
        case LDIV =>
        case DDIV =>
        case LREM =>
        case DREM =>
        case LAND =>
        case LOR =>
        case LXOR =>
        // (int, int) -> ()
        case IF_ICMPEQ =>
        case IF_ICMPNE =>
        case IF_ICMPLT =>
        case IF_ICMPGE =>
        case IF_ICMPGT =>
        case IF_ICMPLE =>
        case LRETURN =>
        case DRETURN =>
          stack -= 2

        // (ref, index, value) -> ()
        case IASTORE =>
        case FASTORE =>
        case AASTORE =>
        case BASTORE =>
        case CASTORE =>
        case SASTORE =>
        // (long, long) -> (int)
        case LCMP =>
        // (double, double) -> (int)
        case DCMPL =>
        case DCMPG =>
          stack -= 3

        // (ref, index, long) -> ()
        case LASTORE =>
        // (ref, index, double) -> ()
        case DASTORE =>
          stack -= 4

        case GETSTATIC =>
          stack += getFieldStackSize(instruction)

        case PUTSTATIC =>
          stack -= getFieldStackSize(instruction)

        case GETFIELD =>
          stack += getFieldStackSize(instruction) - 1 // ref

        case PUTFIELD =>
          stack -= getFieldStackSize(instruction) + 1

        case INVOKEVIRTUAL =>
        case INVOKESPECIAL =>
        case INVOKEINTERFACE =>
          stack += getMethodStackSize(instruction) - 1

        case INVOKEDYNAMIC =>
        case INVOKESTATIC =>
          stack += getMethodStackSize(instruction)

        case MULTIANEWARRAY =>
          stack -= instruction.asInstanceOf[MultiANewArrayInsnNode].dims - 1
        case _ =>
      }
    })
    instructionStackMap
  }

  private def getFieldStackSize(abstractInsnNode: AbstractInsnNode) = {
    val fieldInsnNode = abstractInsnNode.asInstanceOf[FieldInsnNode]
    Type.getType(fieldInsnNode.desc).getSize
  }

  private def getMethodStackSize(abstractInsnNode: AbstractInsnNode) = {
    var size = 0
    var parameters: Array[Type] = null
    var returnType: Type = null
    abstractInsnNode match {
      case methodInsnNode: MethodInsnNode =>
        parameters = Type.getArgumentTypes(methodInsnNode.desc)
        returnType = Type.getReturnType(methodInsnNode.desc)
      case _ =>
        val invokeDynamicInsnNode = abstractInsnNode.asInstanceOf[InvokeDynamicInsnNode]
        parameters = Type.getArgumentTypes(invokeDynamicInsnNode.desc)
        returnType = Type.getReturnType(invokeDynamicInsnNode.desc)
    }
    parameters.foreach(parameter => size -= parameter.getSize)
    size += returnType.getSize
    size
  }

}
