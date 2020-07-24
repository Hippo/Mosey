package rip.hippo.mosey.transformer.impl.misc

import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.transformer.Transformer
import org.objectweb.asm.Opcodes._

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 */
final class SyntheticBridgeTransformer extends Transformer {

  override def transform(classWrapper: ClassWrapper): Unit = {
    classWrapper.methods.foreach(method => {
      if (!method.getName.startsWith("<") && !method.hasModifier(ACC_BRIDGE)) method.addModifier(ACC_BRIDGE)
      if (!method.hasModifier(ACC_SYNTHETIC)) method.addModifier(ACC_SYNTHETIC)

    })
    classWrapper.fields.filter(field => !field.hasModifier(ACC_SYNTHETIC)).foreach(field => field.addModifier(ACC_SYNTHETIC))
  }
}
