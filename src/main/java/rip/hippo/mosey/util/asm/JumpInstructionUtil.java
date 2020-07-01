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

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Hippo
 * @version 1.0.0, 6/30/20
 * @since 1.0.0
 */
public enum JumpInstructionUtil {
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
}
