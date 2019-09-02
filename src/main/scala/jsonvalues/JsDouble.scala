package jsonvalues

case class JsDouble(value: Double) extends JsNumber
{

  override def isInt: Boolean = true

  override def isLong: Boolean = false

  override def isDouble: Boolean = true

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def toString: String = value.toString

}

object JsDouble
{

  import scala.language.implicitConversions

  implicit def of(n: Double): JsDouble =
  {
    JsDouble(n)
  }
}



