package rip.hippo.mosey.jar.resource.impl

import org.objectweb.asm.{ClassReader, ClassWriter}
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.ClassReader._
import org.objectweb.asm.commons.JSRInlinerAdapter
import rip.hippo.mosey.asm.MoseyClassWriter
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.jar.resource.Resource
import rip.hippo.mosey.jar.resource.impl.ClassResource.Bytecode
import rip.hippo.mosey.logger.Logger


/**
 * @author Hippo
 * @version 1.0.0, 7/9/20
 * @since 1.0.0
 */
class ClassResource(bytecode: Bytecode, library: Boolean, inlineJSR: Boolean) extends Resource() {
  private val originalBytecode: Bytecode = bytecode
  private val classNode: ClassNode = new ClassNode()
  new ClassReader(bytecode).accept(classNode, if (library) SKIP_CODE | SKIP_DEBUG | SKIP_FRAMES else 0)

  if (inlineJSR && classNode.version <= V1_5) {
    Logger.info(String.format("Class %s is pre Java 6, inlining JSR instructions.", classNode.name))
    for (i <- 0 until classNode.methods.size()) {
      val methodNode = classNode.methods.get(i)
      val jsrInlinerAdapter = new JSRInlinerAdapter(methodNode, methodNode.access, methodNode.name, methodNode.desc, methodNode.signature, methodNode.exceptions.toArray(Array[String]()))
      methodNode.accept(jsrInlinerAdapter)
      classNode.methods.set(i, jsrInlinerAdapter)
    }
  }

  val classWrapper = new ClassWrapper(classNode)

  override def getName: String = classWrapper.archiveName

  override def toByteArray: Bytecode = {
    val classNode = classWrapper.getClassNode
    Logger.info(String.format("Converting %s.class to obfuscated bytecode, trying to compute frames.", classWrapper.getName))
    var classWriter = MoseyClassWriter()
    try {
      classNode.accept(classWriter)
    } catch {
      case _: Exception =>
        try {
          Logger.warn(String.format("Failed computing frames, attempting to compute maxs (%s.class)", classWrapper.getName))
          classWriter = MoseyClassWriter(ClassWriter.COMPUTE_MAXS)
          classNode.accept(classWriter)
        } catch {
          case _: Exception =>
            try {
              Logger.warn(String.format("Failed computing maxes, attempting with no flags (%s.class)", classWrapper.getName))
              classWriter = MoseyClassWriter(0)
              classNode.accept(classWriter)
            } catch {
              case e: Exception =>
                Logger.error(e, String.format("Failed to write class, resorting to original bytecode (%s.class)", classWrapper.getName))
                return originalBytecode
            }
        }
    }
    try {
      classWriter.toByteArray
    } finally {
      Logger.info(String.format("Successfully written %s.", classWrapper.getName))
    }
  }
}

object ClassResource {
  type Bytecode = Array[Byte]
  def apply(bytecode: Bytecode, library: Boolean, inlineJSR: Boolean): ClassResource = new ClassResource(bytecode, library, inlineJSR)
}