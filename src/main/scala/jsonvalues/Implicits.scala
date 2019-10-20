package jsonvalues

import jsonvalues.JsBool.{FALSE, TRUE}
import scala.language.implicitConversions

object Implicits
{

  implicit def keyJsValueToJsPair[E <: JsValue](pair: (String, E)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexJsValueToJsPair[E <: JsValue](pair: (Int, E)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyStrToJsPair(pair: (String, String)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexStrToJsPair(pair: (Int, String)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyIntToJsPair(pair: (String, Int)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexIntToJsPair(pair: (Int, Int)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyLongToJsPair(pair: (String, Long)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexLongToJsPair(pair: (Int, Long)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyDoubleToJsPair(pair: (String, Double)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexDoubleToJsPair(pair: (Int, Double)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyBigDecToJsPair(pair: (String, BigDecimal)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexBigDecToJsPair(pair: (Int, BigDecimal)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyBoolToJsPair(pair: (String, Boolean)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexBollToJsPair(pair: (Int, Boolean)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyNullToJsPair(pair: (String, Null)): (JsPath, JsValue) = (pair._1, JsNull)

  implicit def indexNullToJsPair(pair: (Int, Null)): (JsPath, JsValue) = (pair._1, JsNull)

  implicit def strToJsValue(str: String): JsStr = JsStr(str)

  implicit def boolToJsValue(bool: Boolean): JsBool = if (bool) TRUE else FALSE

  implicit def longToToJsValue(n: Long): JsLong = JsLong(n)

  implicit def bigDecToJsValue(n: BigDecimal): JsBigDec = JsBigDec(n)

  implicit def bigIntToJsValue(n: BigInt): JsBigInt = JsBigInt(n)

  implicit def doubleToJsValue(n: Double): JsDouble = JsDouble(n)

  implicit def intToJsValue(n: Int): JsInt = JsInt(n)
}
