package jsonvalues

case class JsBigInt(value: BigInt) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = true

  override def isBigDec: Boolean = false
}

object JsBigInt
{

  implicit def of(n: BigInt): JsBigInt =
  {
    JsBigInt(n)
  }
}