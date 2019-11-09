package value

trait JsValue
{
  def isDecimal: Boolean = isDouble || isBigDec

  def isIntegral: Boolean = isInt || isLong || isBigInt

  val isJson: Boolean = isObj || isArr

  def isStr: Boolean

  def isObj: Boolean

  def isArr: Boolean

  def isBool: Boolean

  def isNumber: Boolean

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

  def asJsObj: JsObj

  def asJsStr: JsStr

  def asJsDouble: JsDouble

  def asJsArray: JsArray

  def asJsNumber: JsNumber

  def asJson: Json[_]

}
