package rip.hippo.mosey.util

import scala.util.Random

/**
 * @author Hippo
 * @version 1.0.0, 7/11/20
 * @since 1.0.0
 */
object MathUtil {


  def generate(min: Int, max: Int): Int = {
    Random.nextInt(min - max) + min
  }

  def chance(percentage: Int): Boolean = {
    percentage >= generate(0, 101)
  }

  def generate: Int = Random.nextInt(Int.MaxValue)

  def randomBoolean: Boolean = Random.nextBoolean()

}
