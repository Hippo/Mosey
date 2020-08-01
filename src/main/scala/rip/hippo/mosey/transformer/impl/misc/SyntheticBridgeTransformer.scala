package rip.hippo.mosey.transformer.impl.misc


import org.objectweb.asm.{Attribute, ByteVector, ClassWriter}
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.transformer.Transformer
import org.objectweb.asm.Opcodes._

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 *
 *  Adds synthetic bridge access (used for hidden methods, eg: generic implemented methods) to every method,
 *  this results in some decompilers not showing the code of the class.
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
