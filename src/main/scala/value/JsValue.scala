package value

trait JsValue
{
  def isDecimal: Boolean = isDouble || isBigDec

  def isIntegral: Boolean = isInt || isLong || isBigInt

  def isJson: Boolean = isObj || isArr

  def isNotJson: Boolean = !isJson

  def isStr: Boolean

  def isObj: Boolean

  def isArr: Boolean

  def isBool: Boolean

  def isNumber: Boolean

  def isNotNumber: Boolean = !isNumber

  def isInt: Boolean

  def isLong: Boolean

  def isDouble: Boolean

  def isBigInt: Boolean

  def isBigDec: Boolean

  def isNull: Boolean

  def isNothing: Boolean

  def asJsLong: JsLong

  def asJsInt: JsInt

  def asJsBigInt: JsBigInt

  def asJsBigDec: JsBigDec

  def asJsBool: JsBool

  def asJsNull: JsNull.type

  def asJsObj: JsObj

  def asJsStr: JsStr

  def asJsDouble: JsDouble

  def asJsArray: JsArray

  def asJsNumber: JsNumber

  def asJson: Json[_]

}
