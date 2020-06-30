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

package rip.hippo.mosey.util;

import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Hippo
 * @version 1.0.0, 6/24/20
 * @since 1.0.0
 */
public enum AsmUtil {
    ;

    public static int reverseJump(int opcode) {
        switch (opcode) {
            case IFNE:
                return IFEQ;
            case IFEQ:
                return IFNE;
            case IFGE:
                return IFLT;
            case IFGT:
                return IFLE;
            case IFLE:
                return IFGT;
            case IFLT:
                return IFGE;
            case IFNONNULL:
                return IFNULL;
            case IFNULL:
                return IFNONNULL;
            case IF_ACMPEQ:
                return IF_ACMPNE;
            case IF_ACMPNE:
                return IF_ACMPEQ;
            case IF_ICMPEQ:
                return IF_ICMPNE;
            case IF_ICMPNE:
                return IF_ICMPEQ;
            case IF_ICMPGE:
                return IF_ICMPLT;
            case IF_ICMPGT:
                return IF_ICMPLE;
            case IF_ICMPLE:
                return IF_ICMPGT;
            case IF_ICMPLT:
                return IF_ICMPGE;
            default:
                throw new IllegalStateException(String.format("Unable to reverse jump opcode: %d", opcode));
        }
    }

    public static boolean isDefective(AbstractInsnNode abstractInsnNode) {
        return abstractInsnNode instanceof LabelNode || abstractInsnNode instanceof LineNumberNode || abstractInsnNode instanceof FrameNode || abstractInsnNode.getOpcode() == NOP;
    }

    public static AbstractInsnNode getOptimizedInt(int value) {
        if(value >= -1 && value <= 5) {
            return new InsnNode(toConst(value));
        } else if(value <= Short.MAX_VALUE) {
            return new IntInsnNode((value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) ? BIPUSH : SIPUSH, value);
        }
        return new LdcInsnNode(value);
    }

    public static int toConst(int value) {
        switch (value) {
            case 0: {
                return ICONST_0;
            }
            case 1: {
                return ICONST_1;
            }
            case 2: {
                return ICONST_2;
            }
            case 3: {
                return ICONST_3;
            }
            case 4: {
                return ICONST_4;
            }
            case 5: {
                return ICONST_5;
            }
            case -1: {
                return ICONST_M1;
            }
            default: {
                return -1;
            }
        }
    }

    public static InsnList generateTrashInstructions() {
        int random = MathUtil.randomInt(2);
        InsnList insnList = new InsnList();
        switch (random) {
            case 0: {
                insnList.add(new InsnNode(ICONST_0));
                insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "exit", "(I)V", false));
                break;
            }
            case 1: {
                boolean out = MathUtil.randomBoolean();
                insnList.add(new FieldInsnNode(GETSTATIC, "java/lang/System", out ? "out" : "err", "Ljava/io/PrintStream;"));
                insnList.add(new LdcInsnNode(String.valueOf(System.currentTimeMillis())));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
            }
        }
        return insnList;
    }
}
