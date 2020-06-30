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

package rip.hippo.mosey.transformer;

import rip.hippo.mosey.configuration.Configuration;
import rip.hippo.mosey.dictionary.Dictionary;
import rip.hippo.mosey.dictionary.impl.AlphaNumericDictionary;
import rip.hippo.mosey.jar.resource.ResourceManager;
import rip.hippo.mosey.transformer.impl.flow.FakeJumpTransformer;
import rip.hippo.mosey.transformer.impl.flow.FakeTryCatchesTransformer;
import rip.hippo.mosey.transformer.impl.flow.ReverseJumpTransformer;
import rip.hippo.mosey.transformer.impl.misc.BadAnnotationTransformer;
import rip.hippo.mosey.transformer.impl.misc.SyntheticBridgeTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Hippo
 * @version 1.0.0, 6/24/20
 * @since 1.0.0
 */
public final class TransformerManager {
    private final List<Transformer> enabledTransformers;

    public TransformerManager(Configuration configuration, ResourceManager resourceManager) {
        Map<String, Dictionary> dictionaryMap = new HashMap<>();
        dictionaryMap.put("AlphaNumeric", new AlphaNumericDictionary());
        Dictionary dictionary = dictionaryMap.get(configuration.getDictionary());

        Map<String, Transformer> transformerMap = new HashMap<>();
        transformerMap.put("SyntheticBridge", new SyntheticBridgeTransformer());
        transformerMap.put("ReverseJump", new ReverseJumpTransformer());
        transformerMap.put("FakeTryCatches", new FakeTryCatchesTransformer(configuration));
        transformerMap.put("FakeJump", new FakeJumpTransformer(configuration, dictionary));
        transformerMap.put("BadAnnotation", new BadAnnotationTransformer());

        this.enabledTransformers = configuration.getTransformers().stream().map(transformerMap::get).collect(Collectors.toList());
    }

    public void transform(ClassNode classNode) {
        Logger.info(String.format("Transforming %s", classNode.name));
        for (Transformer transformer : enabledTransformers) {
            transformer.transform(classNode);
        }
        Logger.info(String.format("Successfully transformed %s", classNode.name));
    }
}
