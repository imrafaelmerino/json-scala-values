package jsonvalues

final case class JsBool(value: Boolean) extends JsValue
{
  override def isStr: Boolean = false

  override def isObj: Boolean = false

  override def isArr: Boolean = false

  override def isBool: Boolean = true

  override def isNumber: Boolean = false

  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def isNull: Boolean = false

  override def isNothing: Boolean = false

  override def toString: String = value.toString

}

object JsBool
{
  val TRUE = new JsBool(true)
  val FALSE = new JsBool(false)

}

