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

package rip.hippo.mosey.transformer.impl.flow;

import rip.hippo.mosey.configuration.Configuration;
import rip.hippo.mosey.transformer.Transformer;
import rip.hippo.mosey.util.AsmUtil;
import rip.hippo.mosey.util.MathUtil;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Hippo
 * @version 1.0.0, 6/24/20
 * @since 1.0.0
 *
 * Adds random try catches around instructions.
 *
 * <p>Before</p>
 * <code>
 *     System.out.println("Hello World");
 * </code>
 *
 * <p>After</p>
 * <code>
 *     PrintStream printStream;
 *     try {
 *         printStream = System.out;
 *     } catch (Throwable t) {
 *         throw t;
 *     }
 *     String s;
 *     try {
 *         s = "Hello World";
 *     } catch (Throwable t) {
 *         throw t;
 *     }
 *     printStream.println(s);
 * </code>
 */
public final class FakeTryCatchesTransformer implements Transformer {

    private final int chance;

    public FakeTryCatchesTransformer(Configuration configuration) {
        this.chance = configuration.get("FakeTryCatches", "chance");
    }

    @Override
    public void transform(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            boolean invokedSuper = false;
            for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
                if (method.name.equals("<init>") && !invokedSuper && abstractInsnNode instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                    if(methodInsnNode.name.equals("<init>")) {
                        invokedSuper = true;
                        continue;
                    }
                }
                if ((!method.name.equals("<init>") || invokedSuper) && MathUtil.chance(chance) && !AsmUtil.isDefective(abstractInsnNode)) {
                    LabelNode start = new LabelNode();
                    LabelNode handler = new LabelNode();
                    LabelNode end = new LabelNode();

                    InsnList catchBlock = new InsnList();
                    catchBlock.add(handler);
                    catchBlock.add(new InsnNode(ATHROW));
                    catchBlock.add(end);

                    method.instructions.insertBefore(abstractInsnNode, start);
                    method.instructions.insert(abstractInsnNode, catchBlock);
                    method.instructions.insert(abstractInsnNode, new JumpInsnNode(GOTO, end));
                    method.tryCatchBlocks.add(new TryCatchBlockNode(start, end, handler, "java/lang/Throwable"));
                }
            }
        }
    }
}
