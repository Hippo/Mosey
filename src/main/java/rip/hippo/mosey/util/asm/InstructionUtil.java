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
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;

import static org.objectweb.asm.Opcodes.NOP;

/**
 * @author Hippo
 * @version 1.0.0, 6/30/20
 * @since 1.0.0
 */
public enum InstructionUtil {
    ;

    public static boolean isDefective(AbstractInsnNode abstractInsnNode) {
        return abstractInsnNode instanceof LabelNode || abstractInsnNode instanceof LineNumberNode || abstractInsnNode instanceof FrameNode || abstractInsnNode.getOpcode() == NOP;
    }
}
