package value

import value.JsPath.empty
import scala.language.implicitConversions
import scala.util.{Success, Try}
import JsPath.empty

/**
 * singleton with all the implicit conversions of the library. It must be always imported in order to be
 * more concise and idiomatic defining Jsons, specs and JsPath.
 */
object Preamble
{

  given Conversion[(String, JsValue), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(String, JsInt), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(String, JsStr), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(String, JsLong), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(String, JsDouble), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(String, JsObj), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(String, JsArray), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(String, JsBool), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(String, value.JsNull.type), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)

  given Conversion[(Int, JsInt), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(Int, JsStr), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(Int, JsLong), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(Int, JsDouble), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(Int, JsObj), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(Int, JsArray), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(Int, JsBool), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(Int, value.JsNull.type), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)
  given Conversion[(Int, JsValue), (JsPath, JsValue)] = p => (empty.appended(p._1), p._2)

  given Conversion[(String, Int), (JsPath, JsValue)] = p => (empty.appended(p._1), JsInt(p._2))
  given Conversion[(String, String), (JsPath, JsValue)] = p => (empty.appended(p._1), JsStr(p._2))
  given Conversion[(String, Long), (JsPath, JsValue)] = p => (empty.appended(p._1), JsLong(p._2))
  given Conversion[(String, Double), (JsPath, JsValue)] = p => (empty.appended(p._1), JsDouble(p._2))
  given Conversion[(String, Boolean), (JsPath, JsValue)] = p => (empty.appended(p._1), JsBool(p._2))
  given Conversion[(String, BigDecimal), (JsPath, JsValue)] = p => (empty.appended(p._1), JsBigDec(p._2))
  given Conversion[(String, BigInt), (JsPath, JsValue)] = p => (empty.appended(p._1), JsBigInt(p._2))
  given Conversion[(String, Null), (JsPath, JsValue)] = p => (empty.appended(p._1), JsNull)

  given Conversion[String,JsPath] = empty / _
  given Conversion[Int,JsPath] = empty / _

  given Conversion[Int,JsInt] = JsInt(_)
  given Conversion[Double,JsDouble] = JsDouble(_)
  given Conversion[Long,JsLong] = JsLong(_)
  given Conversion[BigInt,JsBigInt] = JsBigInt(_)
  given Conversion[BigDecimal,JsBigDec] = JsBigDec(_)
  given Conversion[Boolean,JsBool] = JsBool(_)
  given Conversion[String,JsStr] = JsStr(_)
}
