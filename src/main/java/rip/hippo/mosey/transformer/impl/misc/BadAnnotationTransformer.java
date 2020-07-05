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

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import rip.hippo.mosey.asm.wrapper.ClassWrapper;
import rip.hippo.mosey.transformer.Transformer;

import java.util.ArrayList;

/**
 * @author Hippo
 * @version 1.0.0, 6/30/20
 * @since 1.0.0
 *
 * Inserts null descriptor annotations which will result in most decompilers crashing.
 */
public final class BadAnnotationTransformer implements Transformer {

    @Override
    public void transform(ClassWrapper classWrapper) {
        classWrapper.addVisibleAnnotation(new AnnotationNode(""));
        classWrapper.methods().forEach(method -> method.addVisibleAnnotation(new AnnotationNode("")));
        classWrapper.fields().forEach(field -> field.addVisibleAnnotation(new AnnotationNode("")));
    }
}
