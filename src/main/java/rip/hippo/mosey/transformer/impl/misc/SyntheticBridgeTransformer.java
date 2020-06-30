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

package rip.hippo.mosey.transformer.impl.misc;

import rip.hippo.mosey.transformer.Transformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.ACC_BRIDGE;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;

/**
 * @author Hippo
 * @version 1.0.0, 6/24/20
 * @since 1.0.0
 *
 * This transformer adds the synthetic bridge modifier to methods (and synthetic to fields), which hides them from some decompilers.
 */
public final class SyntheticBridgeTransformer implements Transformer {

    @Override
    public void transform(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            if(!method.name.startsWith("<") && (method.access & ACC_BRIDGE) == 0) {
                method.access |= ACC_BRIDGE;

            }
            if((method.access & ACC_SYNTHETIC) == 0) {
                method.access |= ACC_SYNTHETIC;
            }
        }
        for (FieldNode field : classNode.fields) {
            if((field.access & ACC_SYNTHETIC) == 0) {
                field.access |= ACC_SYNTHETIC;
            }
        }
    }
}
