package value

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

  override def asJsLong: JsLong = throw UserError.asJsLongOfJsNull

  override def asJsNull: JsNull.type = this

  override def asJsStr: JsStr = throw UserError.asJsStrOfJsNull

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsNull

  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJsNull

  override def asJsBigDec: JsBigDec = throw UserError.asJsBigDecOfJsNull

  override def asJsBool: JsBool = throw UserError.asJsBoolOfJsNull

  override def asJsObj: JsObj = throw UserError.asJsObjOfJsNull

  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsNull

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJsNull

  override def asJsNumber: JsNumber = throw UserError.asJsNumberOfJsNull

  override def asJson: Json[_] = throw UserError.asJsonOfJsNull

}
