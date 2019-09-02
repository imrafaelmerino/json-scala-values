package jsonvalues

case class JsInt(value:Int) extends JsNumber {

  override def isInt: Boolean = true

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def toString: String = value.toString

}

object JsInt{

  implicit def of(n:Int): JsInt = {
    JsInt(n)
  }
}
