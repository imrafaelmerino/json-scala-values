package value

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

  override def asJsLong: JsLong = throw UserError.asJsLongOfJsStr

  override def asJsNull: JsNull.type = throw UserError.asJsNullOfJsStr

  override def asJsStr: JsStr = this

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsStr

  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJsStr

  override def asJsBigDec: JsBigDec = throw UserError.asJsBigDecOfJsStr

  override def asJsBool: JsBool = throw UserError.asJsBoolOfJsStr

  override def asJsObj: JsObj = throw UserError.asJsObjOfJsStr

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJsStr

  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsStr

  override def toString: String = s"""\"$value\""""

  override def asJsNumber: JsNumber = throw UserError.asJsNumberOfJsStr

  def map(m: String => String): JsStr = JsStr(m(value))

  override def asJson: Json[_] = throw UserError.asJsonOfJsStr

}
