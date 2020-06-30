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

package rip.hippo.mosey.jar.resource.impl;


import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.MethodNode;
import rip.hippo.mosey.asm.MoseyClassWriter;
import rip.hippo.mosey.jar.resource.Resource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import static org.objectweb.asm.Opcodes.*;
import org.tinylog.Logger;

/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public final class ClassResource implements Resource {

    private final ClassNode classNode;
    private final byte[] originalBytecode;

    public ClassResource(byte[] bytecode) {
        this.classNode = new ClassNode();
        this.originalBytecode = bytecode;
        new ClassReader(bytecode).accept(classNode, 0);

        if (classNode.version <= V1_5) {
            Logger.info(String.format("Class %s is pre Java 6, inlining JSR instructions.", classNode.name));
            for (int i = 0; i < classNode.methods.size(); i++) {
                MethodNode methodNode = classNode.methods.get(i);
                JSRInlinerAdapter jsrInlinerAdapter = new JSRInlinerAdapter(methodNode, methodNode.access, methodNode.name, methodNode.desc, methodNode.signature, methodNode.exceptions.toArray(new String[0]));
                methodNode.accept(jsrInlinerAdapter);
                classNode.methods.set(i, jsrInlinerAdapter);
            }
        }
    }

    @Override
    public byte[] toByteArray() {
        Logger.info(String.format("Converting %s.class to obfuscated bytecode, trying to compute frames.", classNode.name));
        ClassWriter classWriter;
        try {
            classWriter = new MoseyClassWriter(ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
        } catch (Exception e) {
            try {
                Logger.warn(String.format("Failed computing frames, attempting to compute maxs (%s.class)", classNode.name));
                classWriter = new MoseyClassWriter(ClassWriter.COMPUTE_MAXS);
                classNode.accept(classWriter);
            } catch (Exception e1) {
                try {
                    Logger.warn(String.format("Failed computing maxes, attempting with no flags (%s.class)", classNode.name));
                    classWriter = new MoseyClassWriter(0);
                    classNode.accept(classWriter);
                } catch (Exception e2) {
                    Logger.error(e2, String.format("Failed to write class, resorting to original bytecode (%s.class)", classNode.name));
                    return originalBytecode;
                }
            }
        }
        try {
            return classWriter.toByteArray();
        } finally {
            Logger.info(String.format("Successfully written %s.", classNode.name));
        }
    }

    @Override
    public String getName() {
        return String.format("%s.class", classNode.name);
    }

    public ClassNode getClassNode() {
        return classNode;
    }

}
