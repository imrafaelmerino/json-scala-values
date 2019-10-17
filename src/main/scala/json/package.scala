import json.JsBool.{FALSE, TRUE}
import json.JsPath./

import scala.language.implicitConversions

package object json
{
  type JsPair = (JsPath, JsValue)

  implicit def keyJsValueToJsPair[E <: JsValue](pair: (String, E)): JsPair = (pair._1, pair._2)

  implicit def indexJsValueToJsPair[E <: JsValue](pair: (Int, E)): JsPair = (pair._1, pair._2)

  implicit def keyStrToJsPair(pair: (String, String)): JsPair = (pair._1, pair._2)

  implicit def indexStrToJsPair(pair: (Int, String)): JsPair = (pair._1, pair._2)

  implicit def keyIntToJsPair(pair: (String, Int)): JsPair = (pair._1, pair._2)

  implicit def indexIntToJsPair(pair: (Int, Int)): JsPair = (pair._1, pair._2)

  implicit def keyLongToJsPair(pair: (String, Long)): JsPair = (pair._1, pair._2)

  implicit def indexLongToJsPair(pair: (Int, Long)): JsPair = (pair._1, pair._2)

  implicit def keyDoubleToJsPair(pair: (String, Double)): JsPair = (pair._1, pair._2)

  implicit def indexDoubleToJsPair(pair: (Int, Double)): JsPair = (pair._1, pair._2)

  implicit def keyBigDecToJsPair(pair: (String, BigDecimal)): JsPair = (pair._1, pair._2)

  implicit def indexBigDecToJsPair(pair: (Int, BigDecimal)): JsPair = (pair._1, pair._2)

  implicit def keyBoolToJsPair(pair: (String, Boolean)): JsPair = (pair._1, pair._2)

  implicit def indexBollToJsPair(pair: (Int, Boolean)): JsPair = (pair._1, pair._2)

  implicit def keyNullToJsPair(pair: (String, Null)): JsPair = (pair._1, JsNull)

  implicit def indexNullToJsPair(pair: (Int, Null)): JsPair = (pair._1, JsNull)

  implicit def strToJsValue(str: String): JsStr = JsStr(str)

  implicit def boolToJsValue(bool: Boolean): JsBool = if (bool) TRUE else FALSE

  implicit def longToToJsValue(n: Long): JsLong = JsLong(n)

  implicit def bigDecToJsValue(n: BigDecimal): JsBigDec = JsBigDec(n)

  implicit def bigIntToJsValue(n: BigInt): JsBigInt = JsBigInt(n)

  implicit def doubleToJsValue(n: Double): JsDouble = JsDouble(n)

  implicit def intToJsValue(n: Int): JsInt = JsInt(n)


  def notNull[T](x: T): T =
  {
    if (x == null) throw new NullPointerException()
    else x
  }

}
