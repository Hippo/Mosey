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

import rip.hippo.mosey.analyze.StackSizeAnalyzer;
import rip.hippo.mosey.asm.wrapper.ClassWrapper;
import rip.hippo.mosey.configuration.Configuration;
import rip.hippo.mosey.dictionary.Dictionary;
import rip.hippo.mosey.transformer.Transformer;
import rip.hippo.mosey.util.MathUtil;
import org.objectweb.asm.tree.*;
import rip.hippo.mosey.util.asm.NumberInstructionUtil;
import rip.hippo.mosey.util.asm.ObfuscatedInstructionUtil;


import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Hippo
 * @version 1.0.0, 6/24/20
 * @since 1.0.0
 *
 * <p>Before</p>
 * <code>
 *     if (condition) {
 *         System.out.println("hello world");
 *     }
 * </code>
 *
 * <p>After</p>
 * <code>
 *     if(null == null) {
 *         if (condition) {
 *             if (!"pog".equals("poggers")) {
 *                 System.out.println("hello world");
 *             }
 *         }
 *     } else {
 *         throw null;
 *     }
 * </code>
 */
public final class FakeJumpTransformer implements Transformer {

    private final int chance;
    private final Dictionary dictionary;

    public FakeJumpTransformer(Configuration configuration, Dictionary dictionary) {
        this.chance = configuration.get("FakeJump", "chance");
        this.dictionary = dictionary;
    }

    @Override
    public void transform(ClassWrapper classWrapper) {
        classWrapper.methods().forEach(method -> {
            Map<AbstractInsnNode, Integer> stack = StackSizeAnalyzer.emulateStack(method);

            for (AbstractInsnNode abstractInsnNode : method.getInstructions().toArray()) {
                if (stack.get(abstractInsnNode) == 0 && MathUtil.chance(chance)) { // asm isn't able to compute stack map frames properly if there is a junk jump when the stack size isn't 0
                    LabelNode labelNode = new LabelNode();
                    InsnList insnList = new InsnList();
                    int random = MathUtil.randomInt(4);
                    boolean follow = MathUtil.randomBoolean();
                    switch (random) {
                        case 0:
                            insnList.add(new InsnNode(ACONST_NULL));
                            insnList.add(new JumpInsnNode(follow ? IFNULL : IFNONNULL, labelNode));
                            break;
                        case 1:
                            insnList.add(new InsnNode(ICONST_0));
                            insnList.add(new JumpInsnNode(follow ? IFEQ : IFNE, labelNode));
                            break;
                        case 2:
                            insnList.add(NumberInstructionUtil.getOptimizedInt(MathUtil.generate(1, Integer.MAX_VALUE)));
                            insnList.add(new JumpInsnNode(follow ? IFNE : IFEQ, labelNode));
                            break;
                        case 3:
                            String first = dictionary.generate(MathUtil.generate(8, 16));
                            insnList.add(new LdcInsnNode(first));
                            String second = dictionary.generate(MathUtil.generate(8, 16));
                            while (second.equals(first)) {
                                second = dictionary.generate(MathUtil.generate(8, 16));
                            }
                            insnList.add(new LdcInsnNode(second));
                            insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
                            insnList.add(new JumpInsnNode(follow ? IFEQ : IFNE, labelNode));
                    }
                    method.getInstructions().insertBefore(abstractInsnNode, insnList);
                    if (follow) {
                        method.getInstructions().insertBefore(abstractInsnNode, ObfuscatedInstructionUtil.generateTrashInstructions());
                        method.getInstructions().insertBefore(abstractInsnNode, labelNode);
                    } else {
                        method.getInstructions().add(labelNode);
                        method.getInstructions().add(new InsnNode(ACONST_NULL));
                        method.getInstructions().add(new InsnNode(ATHROW));
                    }

                }
            }
        });
    }
}
