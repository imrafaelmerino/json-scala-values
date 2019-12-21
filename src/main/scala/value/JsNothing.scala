package value

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

  override def asJsLong: JsLong = throw UserError.asJsLongOfJsNothing

  override def asJsNull: JsNull.type = throw UserError.asJsNullOfJsNothing

  override def asJsStr: JsStr = throw UserError.asJsStrOfJsNothing

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsNothing

  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJsNothing

  override def asJsBigDec: JsBigDec = throw UserError.asJsBigDecOfJsNothing

  override def asJsBool: JsBool = throw UserError.asJsBoolOfJsNothing

  override def asJsObj: JsObj = throw UserError.asJsObjOfJsNothing

  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsNothing

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJsNothing

  override def asJsNumber: JsNumber = throw UserError.asJsNumberOfJsNothing

  override def asJson: Json[_] = throw UserError.asJsonOfJsNothing

  override def id: Int = 10
}
