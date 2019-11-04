package jsonvalues

final case class JsStr(value: String) extends JsValue
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

  override def asJsLong: JsLong = throw new UnsupportedOperationException("asJsLong of JsStr")

  override def asJsStr: JsStr = this

  override def asJsInt: JsInt = throw new UnsupportedOperationException("asJsInt of JsStr")

  override def asJsBigInt: JsBigInt = throw new UnsupportedOperationException("asJsBigInt of JsStr")

  override def asJsBigDec: JsBigDec = throw new UnsupportedOperationException("asJsBigDec of JsStr")

  override def asJsBool: JsBool = throw new UnsupportedOperationException("asJsBool of JsStr")

  override def asJsObj: JsObj = throw new UnsupportedOperationException("asJsObj of JsStr")

  override def asJsDouble: JsDouble = throw new UnsupportedOperationException("asJsDouble of JsStr")

  override def  asJsArray: JsArray = throw new UnsupportedOperationException("asJsArray of JsStr")

  override def toString: String = s"""\"$value\""""

  override def asJsNumber: JsNumber = throw new UnsupportedOperationException("asJsNumber of JsStr")

  def map(m:String=>String):JsStr = JsStr(m(value))

  override def asJson: Json[_] = throw new UnsupportedOperationException("asJson of JsStr")

}
