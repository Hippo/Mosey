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

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.*;
import rip.hippo.mosey.analyze.StackSizeAnalyzer;
import rip.hippo.mosey.asm.wrapper.ClassWrapper;
import rip.hippo.mosey.configuration.Configuration;
import rip.hippo.mosey.transformer.Transformer;
import rip.hippo.mosey.util.MathUtil;
import rip.hippo.mosey.util.asm.NumberInstructionUtil;

import java.util.Map;

/**
 * @author Hippo
 * @version 1.0.0, 7/1/20
 * @since 1.0.0
 *
 * Wraps certain instructions with junk switch statements.
 * NOTE: not complete yet, only supports integers, finish later
 */
public final class ConfusingSwitchTransformer implements Transformer {

    private final boolean constants;
    private final int chance;

    public ConfusingSwitchTransformer(Configuration configuration) {
        this.constants = configuration.get("ConfusingSwitch", "constants");
        this.chance = configuration.get("ConfusingSwitch", "chance");
    }

    @Override
    public void transform(ClassWrapper classWrapper) {
        classWrapper.methods()
                .addFilter(method -> !method.getName().equals("<init>"))
                .forEach(method -> {
                    Map<AbstractInsnNode, Integer> stack = StackSizeAnalyzer.emulateStack(method);

                    int first = MathUtil.generate();
                    int second = MathUtil.generate();
                    int firstIndex = method.getMaxLocals();
                    int secondIndex = firstIndex + 1;
                    int free = secondIndex + 1;

                    InsnList setTrash = new InsnList();
                    setTrash.add(NumberInstructionUtil.getOptimizedInt(first));
                    setTrash.add(new VarInsnNode(ISTORE, firstIndex));
                    setTrash.add(NumberInstructionUtil.getOptimizedInt(second));
                    setTrash.add(new VarInsnNode(ISTORE, secondIndex));

                    for (AbstractInsnNode abstractInsnNode : method.getInstructions().toArray()) {
                        Integer extracted = NumberInstructionUtil.extractInteger(abstractInsnNode);
                        if(extracted != null && stack.get(abstractInsnNode) == 0 && constants && MathUtil.chance(chance)) {
                            InsnList trapSwitch = new InsnList();
                            LabelNode real = new LabelNode();
                            LabelNode fake = new LabelNode();
                            LabelNode dflt = new LabelNode();
                            int realValue = first ^ second;
                            int offset = MathUtil.generate(1, realValue / 2);
                            int fakeValue = realValue + (MathUtil.randomBoolean() ? -offset : offset);
                            boolean realFirst = MathUtil.randomBoolean();

                            trapSwitch.add(new VarInsnNode(ILOAD, firstIndex));
                            trapSwitch.add(new VarInsnNode(ILOAD, secondIndex));
                            trapSwitch.add(new InsnNode(IXOR));
                            trapSwitch.add(new VarInsnNode(ISTORE, free));
                            trapSwitch.add(new VarInsnNode(ILOAD, free));
                            trapSwitch.add(new LookupSwitchInsnNode(dflt,
                                    new int[] {realFirst ? realValue : fakeValue, realFirst ? fakeValue : realValue},
                                    new LabelNode[] {realFirst ? real : fake, realFirst ? fake : real}));

                            InsnList realHandler = new InsnList();
                            realHandler.add(real);
                            realHandler.add(NumberInstructionUtil.getOptimizedInt(extracted));
                            realHandler.add(new VarInsnNode(ISTORE, free));
                            realHandler.add(new JumpInsnNode(GOTO, dflt));

                            InsnList fakeHandler = new InsnList();
                            fakeHandler.add(fake);
                            int extractedAbs = Math.abs(extracted);
                            if (extractedAbs == 0) {
                                extractedAbs = MathUtil.generate(8, 32);
                            }
                            int half = extractedAbs / 2;
                            int trash = MathUtil.generate(half, extractedAbs + half);
                            fakeHandler.add(NumberInstructionUtil.getOptimizedInt(MathUtil.randomBoolean() ? trash : -trash));
                            fakeHandler.add(new VarInsnNode(ISTORE, free));
                            fakeHandler.add(new JumpInsnNode(GOTO, dflt));

                            if (realFirst) {
                                trapSwitch.add(realHandler);
                                trapSwitch.add(fakeHandler);
                            } else {
                                trapSwitch.add(fakeHandler);
                                trapSwitch.add(realHandler);
                            }
                            trapSwitch.add(dflt);
                            trapSwitch.add(new VarInsnNode(ILOAD, free));

                            method.getInstructions().insert(abstractInsnNode, trapSwitch);
                            method.getInstructions().remove(abstractInsnNode);
                        }
                    }

                    method.getInstructions().insert(setTrash);
                });
    }
}
