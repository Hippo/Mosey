package rip.hippo.mosey.transformer

import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.configuration.Configuration
import rip.hippo.mosey.dictionary.Dictionary
import rip.hippo.mosey.dictionary.impl.AlphaNumericDictionary
import rip.hippo.mosey.jar.resource.ResourceManager
import rip.hippo.mosey.logger.Logger
import rip.hippo.mosey.transformer.impl.data.StringEncryptionTransformer
import rip.hippo.mosey.transformer.impl.exploits.{BadAttributeTransformer, ClassEntryHiderTransformer, InvalidJumpRangeTransformer}
import rip.hippo.mosey.transformer.impl.flow.{ConfusingSwitchTransformer, FakeJumpTransformer, FakeTryCatchesTransformer, ReverseJumpTransformer}
import rip.hippo.mosey.transformer.impl.misc.{BadAnnotationTransformer, SyntheticBridgeTransformer}

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 */
final class TransformerManager(configuration: Configuration, resourceManager: ResourceManager) {
  private val dictionaryMap = Map[String, Dictionary]("AlphaNumeric" -> new AlphaNumericDictionary)
  private val dictionary = dictionaryMap.get(configuration.getDictionary) match {
    case None => dictionaryMap("AlphaNumeric")
    case Some(value) => value
  }
  private val transformerMap = Map[String, Transformer](
    "SyntheticBridge" -> new SyntheticBridgeTransformer,
    "ReverseJump" -> new ReverseJumpTransformer,
    "FakeTryCatches" -> new FakeTryCatchesTransformer(configuration),
    "FakeJump" -> new FakeJumpTransformer(configuration, dictionary),
    "BadAnnotation" -> new BadAnnotationTransformer,
    "ConfusingSwitch" -> new ConfusingSwitchTransformer(configuration),
    "JumpRange" -> new InvalidJumpRangeTransformer,
    "ClassEntryHider" -> new ClassEntryHiderTransformer,
    "BadAttribute" -> new BadAttributeTransformer(configuration),
    "StringEncryption" -> new StringEncryptionTransformer(configuration, dictionary)
  )

  private val enabledTransformers = configuration.getTransformers.map(name => transformerMap(name))

  def transform(classWrapper: ClassWrapper): Unit = {
    Logger.info(String.format("Transforming %s", classWrapper.getName))
    enabledTransformers.foreach(transformer => transformer.transform(classWrapper))
    Logger.info(String.format("Successfully transformed %s", classWrapper.getName))
  }
}
