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

import rip.hippo.mosey.asm.wrapper.ClassWrapper;
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
    public void transform(ClassWrapper classWrapper) {
        classWrapper.applyMethods(method -> {
            if (!method.getName().startsWith("<") && !method.hasModifier(ACC_BRIDGE)) {
                method.addModifier(ACC_BRIDGE);

            }
            if (!method.hasModifier(ACC_SYNTHETIC)) {
                method.addModifier(ACC_SYNTHETIC);
            }
        });

        classWrapper.applyFields(field -> {
            if (!field.hasModifier(ACC_SYNTHETIC)) {
                field.addModifier(ACC_SYNTHETIC);
            }
        });
    }
}
