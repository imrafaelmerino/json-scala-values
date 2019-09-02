package jsonvalues

case class JsBigDec(value: BigDecimal) extends JsNumber
{
  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = true
}

object JsBigDec {
  implicit def of(n:BigDecimal): JsBigDec = {
    JsBigDec(n)
  }
}
