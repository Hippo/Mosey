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


import org.tinylog.Logger;
import rip.hippo.mosey.asm.wrapper.ClassWrapper;

import java.util.*;

/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public enum ClassHierarchy {
    ;
    private static final Map<String, ClassWrapper> CLASS_LOOKUP_MAP = new HashMap<>();
    private static final Map<ClassWrapper, ClassWrapper> CLASS_HIERARCHY_MAP = new HashMap<>();
    
    public static void registerClass(ClassWrapper classWrapper) {
        CLASS_LOOKUP_MAP.put(classWrapper.getName(), classWrapper);
    }
    
    public static ClassWrapper lookup(String name) {
        return CLASS_LOOKUP_MAP.get(name);
    }

    public static void registerAncestors() {
        for (ClassWrapper classWrapper : CLASS_LOOKUP_MAP.values()) {
            setSuperClass(classWrapper, classWrapper.getSuperName());
        }
    }
    
    private static void setSuperClass(ClassWrapper classWrapper, String name) {
        ClassWrapper lookup = lookup(name);
        if(lookup == null) {
            Logger.warn(String.format("Class %s can't be found, ensure all libraries are loaded, ignoring.", name));
            return;
        }
        CLASS_HIERARCHY_MAP.put(classWrapper, lookup);
    }

    public static ClassWrapper getSuperClass(ClassWrapper classWrapper) {
        return CLASS_HIERARCHY_MAP.get(classWrapper);
    }
}
