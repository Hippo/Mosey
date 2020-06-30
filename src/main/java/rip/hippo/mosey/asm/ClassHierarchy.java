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

package rip.hippo.mosey.asm;


import org.objectweb.asm.tree.ClassNode;
import org.tinylog.Logger;

import java.util.*;

/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public enum ClassHierarchy {
    ;
    private static final Map<String, ClassNode> CLASS_LOOKUP_MAP = new HashMap<>();
    private static final Map<ClassNode, ClassNode> CLASS_HIERARCHY_MAP = new HashMap<>();
    
    public static void registerClass(ClassNode classNode) {
        CLASS_LOOKUP_MAP.put(classNode.name, classNode);
    }
    
    public static ClassNode lookup(String name) {
        return CLASS_LOOKUP_MAP.get(name);
    }

    public static void registerAncestors() {
        for (ClassNode classNode : CLASS_LOOKUP_MAP.values()) {
            setSuperClass(classNode, classNode.superName);
        }
    }
    
    private static void setSuperClass(ClassNode classNode, String name) {
        ClassNode lookup = lookup(name);
        if(lookup == null) {
            Logger.warn(String.format("Class %s can't be found, ensure all libraries are loaded, ignoring.", name));
            return;
        }
        CLASS_HIERARCHY_MAP.put(classNode, lookup);
    }

    public static ClassNode getSuperClass(ClassNode classNode) {
        return CLASS_HIERARCHY_MAP.get(classNode);
    }
}
