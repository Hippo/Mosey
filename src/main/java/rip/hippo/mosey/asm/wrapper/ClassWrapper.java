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

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import rip.hippo.mosey.util.FilteredForeachAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Hippo
 * @version 1.0.0, 7/1/20
 * @since 1.0.0
 */
public final class ClassWrapper {

    private final ClassNode classNode;
    private final List<MethodWrapper> methods;
    private final List<FieldWrapper> fields;

    public ClassWrapper(ClassNode classNode) {
        this.classNode = classNode;
        this.methods = new LinkedList<>();
        this.fields = new LinkedList<>();

        classNode.methods.stream().map(MethodWrapper::new).forEach(methods::add);
        classNode.fields.stream().map(FieldWrapper::new).forEach(fields::add);
    }

    public void addVisibleAnnotation(AnnotationNode annotationNode) {
        if(classNode.visibleAnnotations == null) {
            classNode.visibleAnnotations = new ArrayList<>(1);
        }
        classNode.visibleAnnotations.add(annotationNode);
    }

    public FilteredForeachAdapter<MethodWrapper> methods() {
        return new FilteredForeachAdapter<>(methods);
    }

    public FilteredForeachAdapter<FieldWrapper> fields() {
        return new FilteredForeachAdapter<>(fields);
    }

    public boolean hasModifier(int modifier) {
        return (classNode.access & modifier) != 0;
    }

    public String getName() {
        return classNode.name;
    }

    public String getSuperName() {
        return classNode.superName;
    }

    public ClassNode getClassNode() {
        return classNode;
    }
}
