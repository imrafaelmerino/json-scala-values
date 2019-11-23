package value
import java.util.Objects.requireNonNull
trait JsValue
{

  def isDecimal: Boolean = isDouble || isBigDec

  def isIntegral: Boolean = isInt || isLong || isBigInt

  def isJson: Boolean = isObj || isArr

  def isJson(p: Json[_] => Boolean): Boolean = isJson && requireNonNull(p)(asJson)

  def isNotJson: Boolean = !isJson

  def isStr: Boolean

  def isStr(p   : String => Boolean): Boolean = isStr && requireNonNull(p)(asJsStr.value)

  def isObj: Boolean

  def isObj(p: JsObj => Boolean): Boolean = isObj && requireNonNull(p)(asJsObj)

  def isArr: Boolean

  def isArr(p: JsArray => Boolean): Boolean = isArr && requireNonNull(p)(asJsArray)

  def isBool: Boolean

  def isNumber: Boolean

  def isNotNumber: Boolean = !isNumber

  def isInt: Boolean

  def isInt(p: Int => Boolean): Boolean = isInt && requireNonNull(p)(asJsInt.value)

  def isLong: Boolean

  def isLong(p: Long => Boolean): Boolean = isLong && requireNonNull(p)(asJsLong.value)

  def isDouble: Boolean

  def isDouble(p: Double => Boolean): Boolean = isDouble && requireNonNull(p)(asJsDouble.value)

  def isBigInt: Boolean

  def isBigInt(p: BigInt => Boolean): Boolean = isBigInt && requireNonNull(p)(asJsBigInt.value)

  def isBigDec: Boolean

  def isBigDec(p: BigDecimal => Boolean): Boolean = isBigDec && requireNonNull(p)(asJsBigDec.value)

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
