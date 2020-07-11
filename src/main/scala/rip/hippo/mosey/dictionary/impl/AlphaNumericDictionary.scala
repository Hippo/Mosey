package rip.hippo.mosey.dictionary.impl

import rip.hippo.mosey.dictionary.Dictionary

import scala.collection.mutable
import scala.util.Random

/**
 * @author Hippo
 * @version 1.0.0, 7/10/20
 * @since 1.0.0
 */
final class AlphaNumericDictionary extends Dictionary {

  private val reserved: mutable.Set[String] = mutable.Set()

  override def generate(length: Int): String = {
    Random.alphanumeric.take(length).mkString
  }

  override def generateUnique(length: Int): String = {
    var generated = generateUnique(length)
    var times = 0
    var currentLength = length;
    while (reserved.contains(generated)) {
      generated = generate(currentLength)
      if ({
        times += 1
        times - 1
      } > 10) {
        currentLength += 1
      }
    }
    generated
  }
}
