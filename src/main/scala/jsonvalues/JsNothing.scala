package jsonvalues

case object JsNothing extends JsValue
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

  override def isNull: Boolean = false

  override def isNothing: Boolean = true

  override def asJsLong: JsLong = throw new UnsupportedOperationException("asJsLong of JsNothing")

  override def asJsStr: JsStr = throw new UnsupportedOperationException("asJsStr of JsNothing")

  override def asJsInt: JsInt = throw new UnsupportedOperationException("asJsInt of JsNothing")

  override def asJsBigInt: JsBigInt = throw new UnsupportedOperationException("asJsBigInt of JsNothing")

  override def asJsBigDec: JsBigDec = throw new UnsupportedOperationException("asJsBigDec of JsNothing")

  override def asJsBool: JsBool = throw new UnsupportedOperationException("asJsBool of JsNothing")

  override def asJsObj: JsObj = throw new UnsupportedOperationException("asJsObj of JsNothing")

  override def asJsArray: JsArray = throw new UnsupportedOperationException("asJsArray of JsNothing")

  override def asJsDouble: JsDouble = throw new UnsupportedOperationException("asJsDouble of JsNothing")

  override def asJsNumber: JsNumber = throw new UnsupportedOperationException("asJsNumber of JsNothing")

  override def asJson: Json[_] = throw new UnsupportedOperationException("asJson of JsNothing")


}

