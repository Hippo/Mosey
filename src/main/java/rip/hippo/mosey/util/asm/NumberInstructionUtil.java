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

package rip.hippo.mosey.util.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ICONST_5;

/**
 * @author Hippo
 * @version 1.0.0, 6/30/20
 * @since 1.0.0
 */
public enum NumberInstructionUtil {
    ;

    public static AbstractInsnNode getOptimizedInt(int value) {
        if(value >= -1 && value <= 5) {
            return new InsnNode(toConst(value));
        } else if(value <= Short.MAX_VALUE) {
            return new IntInsnNode((value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) ? BIPUSH : SIPUSH, value);
        }
        return new LdcInsnNode(value);
    }

    public static int toConst(int value) {
        int opcode = value + 3;
        if(opcode >= ICONST_M1 && opcode <= ICONST_5) {
            return opcode;
        }
        throw new IllegalArgumentException(String.format("Value %d can't be converted to a constant opcode.", value));
    }
}
