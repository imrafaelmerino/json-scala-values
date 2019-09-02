package jsonvalues

case class JsStr(value: String) extends JsElem
{
  override def isStr: Boolean = true

  override def isObj: Boolean = false

  override def isArr: Boolean = false

  override def isBool: Boolean = false

  override def isNumber: Boolean = false

  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def isNull: Boolean = false

  override def isNothing: Boolean = false

  override def toString: String = s"""\"$value\""""

}

object JsStr {

  implicit def of(str:String) : JsStr = {
     JsStr(str)
  }

}