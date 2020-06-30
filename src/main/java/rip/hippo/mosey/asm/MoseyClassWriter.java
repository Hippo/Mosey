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

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public final class MoseyClassWriter extends ClassWriter {

    public MoseyClassWriter(int flags) {
        super(flags);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        try {
            return super.getCommonSuperClass(type1, type2);
        }catch (Exception e) {
            if(type1.equals(type2) || type1.equals("java/lang/Object") || type2.equals("java/lang/Object")) {
                return type1;
            }

            ClassNode lookupType1 = ClassHierarchy.lookup(type1);
            ClassNode lookupType2 = ClassHierarchy.lookup(type2);

            if(Modifier.isInterface(lookupType1.access) || Modifier.isInterface(lookupType2.access)) {
                return "java/lang/Object";
            }


            ClassNode lookupType1Super = ClassHierarchy.getSuperClass(lookupType1);
            ClassNode lookupType2Super = ClassHierarchy.getSuperClass(lookupType2);

            if(lookupType1Super.name.equals("java/lang/Object") || lookupType2Super.name.equals("java/lang/Object")) {
                return lookupType1.name;
            }

            if(lookupType2Super.name.equals(lookupType1.name)) {
                return lookupType1.name;
            }
            if(lookupType1Super.name.equals(lookupType2.name)) {
                return lookupType2.name;
            }
            if(lookupType1Super.name.equals(lookupType2Super.name)) {
                return lookupType1Super.name;
            }
            Set<String> treeType1 = tree(lookupType1Super);
            Set<String> treeType2 = tree(lookupType2Super);

            if(treeType1.size() > treeType2.size()) {
                for (String type : treeType2) {
                    if(treeType1.contains(type)) {
                        return type;
                    }
                }
            }else {
                for (String type : treeType1) {
                    if (treeType2.contains(type)) {
                        return type;
                    }
                }
            }
            return "java/lang/Object";
        }
    }

    private Set<String> tree(ClassNode classNode) {
        Set<String> tree = new HashSet<>();
        for(ClassNode superNode = ClassHierarchy.getSuperClass(classNode); !superNode.name.equals("java/lang/Object"); superNode = ClassHierarchy.getSuperClass(superNode)) {
            tree.add(superNode.name);
        }
        return tree;
    }


}
