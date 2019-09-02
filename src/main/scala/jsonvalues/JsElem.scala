package jsonvalues

trait JsElem
{

  val isJson = isObj || isArr;

  def isStr: Boolean;

  def isObj: Boolean;

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

}

object JsElem{

  implicit def asJson(e: JsElem): Json[_] = e.asInstanceOf[Json[_]]

}
