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

import org.objectweb.asm.tree.*;
import rip.hippo.mosey.util.MathUtil;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * @author Hippo
 * @version 1.0.0, 6/30/20
 * @since 1.0.0
 */
public enum ObfuscatedInstructionUtil {
    ;

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
