package jsonvalues

case object JsNull extends JsValue
{
  override def isStr: Boolean = false

  override def isObj: Boolean = false

  override def isArr: Boolean = false

  override def isBool: Boolean = false

  override def isNumber: Boolean = false

  override def isInt: Boolean = false

  override def isLong: Boolean = false

  override def isDouble: Boolean = false

  override def isBigInt: Boolean = false

  override def isBigDec: Boolean = false

  override def isNull: Boolean = true

  override def isNothing: Boolean = false

  override def toString: String = "null"

  override def asJsLong: JsLong = throw new UnsupportedOperationException("asJsLong of JsNull")

  override def asJsStr: JsStr = throw new UnsupportedOperationException("asJsStr of JsNull")

  override def asJsInt: JsInt = throw new UnsupportedOperationException("asJsInt of JsNull")

  override def asJsBigInt: JsBigInt = throw new UnsupportedOperationException("asJsBigInt of JsNull")

  override def asJsBigDec: JsBigDec = throw new UnsupportedOperationException("asJsBigDec of JsNull")

  override def asJsBool: JsBool = throw new UnsupportedOperationException("asJsBool of JsNull")

  override def asJsObj: JsObj = throw new UnsupportedOperationException("asJsObj of JsNull")

  override def asJsArray: JsArray = throw new UnsupportedOperationException("asJsArray of JsNull")

  override def asJsDouble: JsDouble = throw new UnsupportedOperationException("asJsDouble of JsNull")

  override def asJsNumber: JsNumber = throw new UnsupportedOperationException("asJsNumber of JsNull")

  override def asJson: Json[_] = throw new UnsupportedOperationException("asJson of JsNull")

}


