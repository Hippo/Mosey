package rip.hippo.mosey.transformer

import rip.hippo.mosey.asm.wrapper.ClassWrapper

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 */
trait Transformer {
  def transform(classWrapper: ClassWrapper): Unit
}
