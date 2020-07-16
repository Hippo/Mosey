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
        case ACONST_NULL |
             ICONST_M1 |
             ICONST_0 |
             ICONST_1 |
             ICONST_2 |
             ICONST_3 |
             ICONST_4 |
             ICONST_5 |
             FCONST_0 |
             FCONST_1 |
             FCONST_2 |
             BIPUSH |
             SIPUSH |
             LDC |
             ILOAD |
             FLOAD |
             ALOAD |
             DUP |
             DUP_X1 |
             DUP_X2 |
             I2L |
             I2D |
             F2L |
             F2D |
             NEW |
             JSR =>
          instruction match {
            case ldcInsnNode: LdcInsnNode if ldcInsnNode.cst.isInstanceOf[Double] || ldcInsnNode.cst.isInstanceOf[Long] => stack += 1
            case _ if instruction.getOpcode == JSR => Logger.warn("Found JSR instruction while analyzing stack, this may produce undefined behavior.")
            case _ =>
          }
          stack += 1

        case LCONST_0 |
             LCONST_1 |
             DCONST_0 |
             DCONST_1 |
             LLOAD |
             DLOAD |
             DUP2 |
             DUP2_X1 |
             DUP2_X2 => stack += 2

             // (ref, index) -> (value)
        case IALOAD |
             FALOAD |
             AALOAD |
             BALOAD |
             CALOAD |
             SALOAD |
             ISTORE |
             FSTORE |
             POP |
             IADD |
             FADD |
             ISUB |
             FSUB |
             IMUL |
             FMUL |
             IDIV |
             FDIV |
             IREM |
             FREM |
             ISHL |
             ISHR |
             IUSHR |
             // (long, int) -> (result)
             LSHL |
             LSHR |
             LUSHR |
             IAND |
             IOR |
             IXOR |
             L2I |
             L2F |
             D2I |
             D2F |
             // (float, float) -> (int)
             FCMPL |
             FCMPG |
             IFEQ |
             IFNE |
             IFLT |
             IFGE |
             IFGT |
             IFLE |
             IFNULL |
             IFNONNULL |
             // (index) -> ()
             TABLESWITCH |
             // (key) -> ()
             LOOKUPSWITCH |
             IRETURN |
             FRETURN |
             ARETURN |
             ATHROW |
             // (ref) -> ()
             MONITORENTER |
             MONITOREXIT => stack -= 1

        case LSTORE |
             DSTORE |
             POP2 |
             LADD |
             DADD |
             LSUB |
             DSUB |
             LMUL |
             DMUL |
             LDIV |
             DDIV |
             LREM |
             DREM |
             LAND |
             LOR |
             LXOR |
             // (int, int) -> ()
             IF_ICMPEQ |
             IF_ICMPNE |
             IF_ICMPLT |
             IF_ICMPGE |
             IF_ICMPGT |
             IF_ICMPLE |
             LRETURN |
             DRETURN => stack -= 2

             // (ref, index, value) -> ()
        case IASTORE |
             FASTORE |
             AASTORE |
             BASTORE |
             CASTORE |
             SASTORE |
             // (long, long) -> (int)
             LCMP |
             // (double, double) -> (int)
             DCMPL |
             DCMPG => stack -= 3

             // (ref, index, long) -> ()
        case LASTORE |
             // (ref, index, double) -> ()
             DASTORE => stack -= 4

        case GETSTATIC => stack += getFieldStackSize(instruction)

        case PUTSTATIC => stack -= getFieldStackSize(instruction)

        case GETFIELD => stack += getFieldStackSize(instruction) - 1 // ref

        case PUTFIELD => stack -= getFieldStackSize(instruction) + 1

        case INVOKEVIRTUAL |
             INVOKESPECIAL |
             INVOKEINTERFACE => stack += getMethodStackSize(instruction) - 1

        case INVOKEDYNAMIC |
             INVOKESTATIC => stack += getMethodStackSize(instruction)

        case MULTIANEWARRAY => stack -= instruction.asInstanceOf[MultiANewArrayInsnNode].dims - 1

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
