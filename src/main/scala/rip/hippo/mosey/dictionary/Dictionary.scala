package rip.hippo.mosey.dictionary

/**
 * @author Hippo
 * @version 1.0.0, 7/10/20
 * @since 1.0.0
 */
trait Dictionary {
  def generate(length: Int): String
  def generateUnique(length: Int): String
}
