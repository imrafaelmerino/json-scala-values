package value

/**
 * It's a special json value that represents 'nothing'. Inserting nothing in a json leaves the json
 * unchanged. Functions that return a [[JsValue]], return JsNothing when no element is found, what makes
 * them total on their arguments.
 */
case object JsNothing extends JsValue
{
  override def isStr = false

  override def isObj = false

  override def isArr = false

  override def isBool = false

  override def isNumber = false

  override def isInt = false

  override def isLong = false

  override def isDouble = false

  override def isBigInt = false

  override def isBigDec = false

  override def isNull = false

  override def isNothing = true

  override def asJsLong = throw UserError.asJsLongOfJsNothing

  override def asJsNull = throw UserError.asJsNullOfJsNothing

  override def asJsStr = throw UserError.asJsStrOfJsNothing

  override def asJsInt = throw UserError.asJsIntOfJsNothing

  override def asJsBigInt = throw UserError.asJsBigIntOfJsNothing

  override def asJsBigDec = throw UserError.asJsBigDecOfJsNothing

  override def asJsBool = throw UserError.asJsBoolOfJsNothing

  override def asJsObj = throw UserError.asJsObjOfJsNothing

  override def asJsArray = throw UserError.asJsArrayOfJsNothing

  override def asJsDouble = throw UserError.asJsDoubleOfJsNothing

  override def asJsNumber = throw UserError.asJsNumberOfJsNothing

  override def asJson = throw UserError.asJsonOfJsNothing

  override def id = 10
}
