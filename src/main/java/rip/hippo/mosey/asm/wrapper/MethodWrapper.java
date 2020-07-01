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

package rip.hippo.mosey.asm.wrapper;

import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Hippo
 * @version 1.0.0, 7/1/20
 * @since 1.0.0
 */
public final class MethodWrapper {

    private final MethodNode methodNode;

    public MethodWrapper(MethodNode methodNode) {
        this.methodNode = methodNode;
    }

    public void addModifier(int modifier) {
        methodNode.access |= modifier;
    }

    public void removeModifier(int modifier) {
        methodNode.access &= ~modifier;
    }

    public void addVisibleAnnotation(AnnotationNode annotationNode) {
        if (methodNode.visibleAnnotations == null) {
            methodNode.visibleAnnotations = new ArrayList<>(1);
        }
        methodNode.visibleAnnotations.add(annotationNode);
    }

    public boolean hasModifier(int modifier) {
        return (methodNode.access & modifier) != 0;
    }

    public InsnList getInstructions() {
        return methodNode.instructions;
    }

    public List<TryCatchBlockNode> getTryCatchBlocks() {
        return methodNode.tryCatchBlocks;
    }

    public String getName() {
        return methodNode.name;
    }
}
