package value

trait JsValue
{

  def isDecimal: Boolean = isDouble || isBigDec

  def isIntegral: Boolean = isInt || isLong || isBigInt

  def isJson: Boolean = isObj || isArr

  def isJson(p: Json[_] => Boolean): Boolean = isJson && p(asJson)

  def isNotJson: Boolean = !isJson

  def isStr: Boolean

  def isStr(p: String => Boolean): Boolean = isStr && p(asJsStr.value)

  def isObj: Boolean

  def isObj(p: JsObj => Boolean): Boolean = isObj && p(asJsObj)

  def isArr: Boolean

  def isArr(p: JsArray => Boolean): Boolean = isArr && p(asJsArray)

  def isBool: Boolean

  def isNumber: Boolean

  def isNotNumber: Boolean = !isNumber

  def isInt: Boolean

  def isInt(p: Int => Boolean): Boolean = isInt && p(asJsInt.value)

  def isLong: Boolean

  def isLong(p: Long => Boolean): Boolean = isLong && p(asJsLong.value)

  def isDouble: Boolean

  def isDouble(p: Double => Boolean): Boolean = isDouble && p(asJsDouble.value)

  def isBigInt: Boolean

  def isBigInt(p: BigInt => Boolean): Boolean = isBigInt && p(asJsBigInt.value)

  def isBigDec: Boolean

  def isBigDec(p: BigDecimal => Boolean): Boolean = isBigDec && p(asJsBigDec.value)

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

  def mapIfNotNull[T](default: T,
                      map: JsValue => T
                     ): T = if (isNull) default else map(this)

  def mapIfNotNothing[T](default: T,
                         map    : JsValue => T
                        ): T = if (isNothing) default else map(this)

}
