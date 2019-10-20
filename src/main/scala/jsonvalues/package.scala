

import scala.language.implicitConversions

/**
 *
 */
package object jsonvalues
{

  def notNull[T](x: T): T =
  {
    if (x == null) throw new NullPointerException()
    else x
  }

}
