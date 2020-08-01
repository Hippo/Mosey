package rip.hippo.mosey.transformer.impl.data.string

import org.objectweb.asm.tree.{InsnList, LdcInsnNode}
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.transformer.impl.data.StringEncryptionTransformer

/**
 * @author Hippo
 * @version 1.0.0, 7/31/20
 * @since 1.0.0
 */
trait StringEncryptionIntensity {
  def accept(classWrapper: ClassWrapper, parent: StringEncryptionTransformer): Unit
  def encrypt(ldcInsnNode: LdcInsnNode, classWrapper: ClassWrapper): InsnList
}
