package json.value

import json.value.JsPath.empty
import scala.language.implicitConversions
import scala.util.{Success, Try}

/**
 * singleton with all the implicit conversions of the library. It must be always imported in order to be
 * more concise and idiomatic defining Jsons, specs and JsPath.
 */
object Preamble
{
  implicit def keyJsValue2JsPair[E <: JsValue](pair: (String, E)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def indexJsValue2JsPair[E <: JsValue](pair: (Int, E)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyStr2JsPair(pair: (String, String)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyInt2JsPair(pair: (String, Int)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyLong2JsPair(pair: (String, Long)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyDouble2JsPair(pair: (String, Double)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyBigDec2JsPair(pair: (String, BigDecimal)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyBigInt2JsPair(pair: (String, BigInt)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyBool2JsPair(pair: (String, Boolean)): (JsPath, JsValue) = (pair._1, pair._2)

  implicit def keyNull2JsPair(pair: (String, Null)): (JsPath, JsValue) = (pair._1, JsNull)

  implicit def str2JsValue(str: String): JsStr = JsStr(str)

  implicit def bool2JsValue(bool: Boolean): JsBool = if (bool) TRUE else FALSE

  implicit def long2JsValue(n: Long): JsLong = JsLong(n)

  implicit def bigDec2JsValue(n: BigDecimal): JsBigDec = JsBigDec(n)

  implicit def bigInt2JsValue(n: BigInt): JsBigInt = JsBigInt(n)

  implicit def double2JsValue(n: Double): JsDouble = JsDouble(n)

  implicit def int2JsValue(n: Int): JsInt = JsInt(n)

  implicit def str2JsPath(name: String): JsPath =
  {
    empty / name
  }

  implicit def int2JsPath(n: Int): JsPath =
  {
    empty / n
  }
}
