package jsonvalues

case class JsLong(value: Long) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = true

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def toString: String = value.toString

}

object JsLong
{

  implicit def of(n: Long): JsLong =
  {
    JsLong(n)
  }
}