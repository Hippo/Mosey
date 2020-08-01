package rip.hippo.mosey.transformer.impl.misc

import org.objectweb.asm.tree.AnnotationNode
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.transformer.Transformer

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 *
 *  Inserts an annotation with a bad descriptor which results in most decompilers crashing
 */
final class BadAnnotationTransformer extends Transformer {

  override def transform(classWrapper: ClassWrapper): Unit = {
    classWrapper.addVisibleAnnotation(new AnnotationNode(""))
    classWrapper.methods.foreach(method => method.addVisibleAnnotation(new AnnotationNode("")))
    classWrapper.fields.foreach(field => field.addVisibleAnnotation(new AnnotationNode("")))
  }
}
