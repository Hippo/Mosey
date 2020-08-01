package rip.hippo.mosey.transformer.impl.data

import org.objectweb.asm.tree.LdcInsnNode
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.configuration.Configuration
import rip.hippo.mosey.dictionary.Dictionary
import rip.hippo.mosey.transformer.Transformer
import rip.hippo.mosey.transformer.impl.data.string.impl.LightStringEncryptionIntensity

/**
 * @author Hippo
 * @version 1.0.0, 7/31/20
 * @since 1.0.0
 *
 * Well, encrypts strings, nothing more than that eh?
 */
final class StringEncryptionTransformer(configuration: Configuration, val dictionary: Dictionary) extends Transformer {

  private val stringEncryptionIntensity = configuration.get("StringEncryption", "intensity").toString.toLowerCase match {
    case "light" => new LightStringEncryptionIntensity
    case x => throw new IllegalStateException("Could not resolve intensity mode %s".format(x))
  }

  override def transform(classWrapper: ClassWrapper): Unit = {
    val hasStrings = classWrapper.methods.exists(_.getInstructions.toArray.exists(instruction => instruction.isInstanceOf[LdcInsnNode] && instruction.asInstanceOf[LdcInsnNode].cst.isInstanceOf[String]))

    if (hasStrings) {
      stringEncryptionIntensity.accept(classWrapper, this)

      classWrapper.methods.foreach(method =>
        method.getInstructions.toArray.foreach {
          case ldcInsnNode: LdcInsnNode if ldcInsnNode.cst.isInstanceOf[String] =>
            method.getInstructions.insert(ldcInsnNode, stringEncryptionIntensity.encrypt(ldcInsnNode, classWrapper))
            method.getInstructions.remove(ldcInsnNode)
          case _ =>
        }
      )
    }
  }
}
