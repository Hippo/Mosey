package rip.hippo.mosey.resource.impl.resources

import rip.hippo.hippocafe.{ClassFile, ClassReader, ClassWriter}
import rip.hippo.mosey.resource.Resource

import scala.util.{Failure, Success, Using}

/**
 * @author Hippo
 * @version 2.0.0, 6/25/21
 * @since 2.0.0
 */
final case class ClassResource(bytecode: Array[Byte], library: Boolean) extends Resource {
  private val originalBytecode = bytecode

  val classFile: ClassFile =
    Using(new ClassReader(bytecode, library)) {
      classReader =>
        classReader.classFile
    } match {
      case Failure(exception) =>
        throw exception
      case Success(value) =>
        value
    }

  override def getName: String =
    classFile.name.concat(".class")

  override def toByteArray: Array[Byte] = {
    Using(new ClassWriter(classFile).calculateMaxes.generateFrames) {
      classWriter =>
        classWriter.write
    } match {
      case Failure(_) =>
        Using(new ClassWriter(classFile).calculateMaxes) {
          classWriter =>
            classWriter.write
        } match {
          case Failure(_) =>
            Using(new ClassWriter(classFile)) {
              classWriter =>
                classWriter.write
            } match {
              case Failure(_) =>
                originalBytecode
              case Success(value) =>
                value
            }
          case Success(value) =>
            value
        }
      case Success(value) =>
        value
    }
  }
}
