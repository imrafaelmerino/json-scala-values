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

  override def asJsLong: JsLong = throw new UnsupportedOperationException("asJsLong of JsBool")

  override def asJsStr: JsStr = throw new UnsupportedOperationException("asJsStr of JsBool")

  override def asJsInt: JsInt = throw new UnsupportedOperationException("asJsInt of JsBool")

  override def asJsBigInt: JsBigInt = throw new UnsupportedOperationException("asJsBigInt of JsBool")

  override def asJsBigDec: JsBigDec = throw new UnsupportedOperationException("asJsBigDec of JsBool")

  override def asJsBool: JsBool = this

  override def asJsObj: JsObj = throw new UnsupportedOperationException("asJsObj of JsBool")

  override def asJsArray: JsArray = throw new UnsupportedOperationException("asJsArray of JsBool")

  override def asJsDouble: JsDouble = throw new UnsupportedOperationException("asJsDouble of JsBool")

  override def asJsNumber: JsNumber = throw new UnsupportedOperationException("asJsNumber of JsBool")

  override def asJson: Json[_] = throw new UnsupportedOperationException("asJson of JsBool")

}

object JsBool
{
  val TRUE = new JsBool(true)
  val FALSE = new JsBool(false)

}


