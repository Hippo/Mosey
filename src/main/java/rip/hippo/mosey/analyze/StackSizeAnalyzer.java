/*
 * Mosey is a free and open source java bytecode obfuscator.
 *     Copyright (C) 2020  Hippo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rip.hippo.mosey.analyze;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Hippo
 * @version 1.0.0, 6/26/20
 * @since 1.0.0
 */
public enum StackSizeAnalyzer {
    ;

    public static Map<AbstractInsnNode, Integer> emulateStack(MethodNode methodNode) {
        Map<AbstractInsnNode, Integer> instructionStackMap = new HashMap<>();
        List<LabelNode> handlers = methodNode.tryCatchBlocks.stream().map(tcb -> tcb.handler).collect(Collectors.toList());;
        int stack = 0;
        for (AbstractInsnNode abstractInsnNode : methodNode.instructions.toArray()) {
            if(abstractInsnNode instanceof LabelNode && handlers.contains(abstractInsnNode)) {
                stack = 1;
            }
            instructionStackMap.put(abstractInsnNode, stack);

            switch (abstractInsnNode.getOpcode()) {
                case ACONST_NULL:
                case ICONST_M1:
                case ICONST_0:
                case ICONST_1:
                case ICONST_2:
                case ICONST_3:
                case ICONST_4:
                case ICONST_5:
                case FCONST_0:
                case FCONST_1:
                case FCONST_2:
                case BIPUSH:
                case SIPUSH:
                case LDC:
                case ILOAD:
                case FLOAD:
                case ALOAD:
                case DUP:
                case DUP_X1:
                case DUP_X2:
                case I2L:
                case I2D:
                case F2L:
                case F2D:
                case NEW:
                    if(abstractInsnNode instanceof LdcInsnNode) {
                        LdcInsnNode ldcInsnNode = (LdcInsnNode) abstractInsnNode;
                        if(ldcInsnNode.cst instanceof Double || ldcInsnNode.cst instanceof Long) {
                            stack++;
                        }
                    }
                    stack++;
                    break;

                case LCONST_0:
                case LCONST_1:
                case DCONST_0:
                case DCONST_1:
                case LLOAD:
                case DLOAD:
                case DUP2:
                case DUP2_X1:
                case DUP2_X2:
                    stack += 2;
                    break;

                    // (ref, index) -> (value)
                case IALOAD:
                case FALOAD:
                case AALOAD:
                case BALOAD:
                case CALOAD:
                case SALOAD:
                case ISTORE:
                case FSTORE:
                case POP:
                case IADD:
                case FADD:
                case ISUB:
                case FSUB:
                case IMUL:
                case FMUL:
                case IDIV:
                case FDIV:
                case IREM:
                case FREM:
                case ISHL:
                case ISHR:
                case IUSHR:
                    // (long, int) -> (result)
                case LSHL:
                case LSHR:
                case LUSHR:
                case IAND:
                case IOR:
                case IXOR:
                case L2I:
                case L2F:
                case D2I:
                case D2F:
                    // (float, float) -> (int)
                case FCMPL:
                case FCMPG:
                case IFEQ:
                case IFNE:
                case IFLT:
                case IFGE:
                case IFGT:
                case IFLE:
                case IFNULL:
                case IFNONNULL:
                    // (index) -> ()
                case TABLESWITCH:
                    // (key) -> ()
                case LOOKUPSWITCH:
                case IRETURN:
                case FRETURN:
                case ARETURN:
                case ATHROW:
                    // (ref) -> ()
                case MONITORENTER:
                case MONITOREXIT:
                    stack--;
                    break;

                case LSTORE:
                case DSTORE:
                case POP2:
                case LADD:
                case DADD:
                case LSUB:
                case DSUB:
                case LMUL:
                case DMUL:
                case LDIV:
                case DDIV:
                case LREM:
                case DREM:
                case LAND:
                case LOR:
                case LXOR:
                    // (int, int) -> ()
                case IF_ICMPEQ:
                case IF_ICMPNE:
                case IF_ICMPLT:
                case IF_ICMPGE:
                case IF_ICMPGT:
                case IF_ICMPLE:
                case LRETURN:
                case DRETURN:
                    stack -= 2;
                    break;

                    // (ref, index, value) -> ()
                case IASTORE:
                case FASTORE:
                case AASTORE:
                case BASTORE:
                case CASTORE:
                case SASTORE:
                    // (long, long) -> (int)
                case LCMP:
                    // (double, double) -> (int)
                case DCMPL:
                case DCMPG:
                    stack -= 3;
                    break;

                    // (ref, index, long) -> ()
                case LASTORE:
                    // (ref, index, double) -> ()
                case DASTORE:
                    stack -= 4;
                    break;

                case GETSTATIC:
                    stack += getFieldStackSize(abstractInsnNode);
                    break;
                case PUTSTATIC:
                    stack -= getFieldStackSize(abstractInsnNode);
                    break;
                case GETFIELD:
                    stack += getFieldStackSize(abstractInsnNode) - 1; // ref
                    break;
                case PUTFIELD:
                    stack -= getFieldStackSize(abstractInsnNode) + 1; // ref
                    break;
                case INVOKEVIRTUAL:
                case INVOKESPECIAL:
                case INVOKEINTERFACE:
                    stack += getMethodStackSize(abstractInsnNode) - 1; // ref
                    break;
                case INVOKEDYNAMIC:
                case INVOKESTATIC:
                    stack += getMethodStackSize(abstractInsnNode);
                    break;
                case MULTIANEWARRAY:
                    stack -= ((MultiANewArrayInsnNode) abstractInsnNode).dims - 1;
            }
        }
        return instructionStackMap;
    }

    private static int getFieldStackSize(AbstractInsnNode abstractInsnNode) {
        FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNode;
        return Type.getType(fieldInsnNode.desc).getSize();
    }

    private static int getMethodStackSize(AbstractInsnNode abstractInsnNode) {
        int size = 0;
        Type[] parameters;
        Type returnType;
        if(abstractInsnNode instanceof MethodInsnNode) {
            MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
            parameters = Type.getArgumentTypes(methodInsnNode.desc);
            returnType = Type.getReturnType(methodInsnNode.desc);
        }else {
            InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) abstractInsnNode;
            parameters = Type.getArgumentTypes(invokeDynamicInsnNode.desc);
            returnType = Type.getReturnType(invokeDynamicInsnNode.desc);
        }

        for (Type parameter : parameters) {
            size -= parameter.getSize();
        }
        size += returnType.getSize();
        return size;
    }
}
