package value

case class JsBool(value: Boolean) extends JsValue
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

  override def asJsLong: JsLong = throw UserError.asJsLongOfJsBool

  override def asJsStr: JsStr = throw UserError.asJsStrOfJsBool

  override def asJsInt: JsInt = throw UserError.asJsIntOfJsBool

  override def asJsBigInt: JsBigInt = throw UserError.asJsBigIntOfJsBool

  override def asJsBigDec: JsBigDec = throw UserError.asJsBigDecOfJsBool

  override def asJsBool: JsBool = this

  override def asJsNull: JsNull.type = throw UserError.asJsNullOfJsBool

  override def asJsObj: JsObj = throw UserError.asJsObjOfJsBool

  override def asJsArray: JsArray = throw UserError.asJsArrayOfJsBool

  override def asJsDouble: JsDouble = throw UserError.asJsDoubleOfJsBool

  override def asJsNumber: JsNumber = throw UserError.asJsNumberOfJsBool

  override def asJson: Json[_] = throw UserError.asJsonOfJsBool

  override def id: Int = 0
}

object TRUE extends JsBool(true)
object FALSE extends JsBool(false)




