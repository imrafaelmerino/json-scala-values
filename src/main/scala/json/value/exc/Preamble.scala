package json.value.exc

import json.value.{JsArray, JsBigDec, JsBigInt, JsBool, JsDouble, JsInt, JsLong, JsNull, JsObj, JsStr, JsValue}
import scala.language.implicitConversions
import scala.util.{Success, Try}

object Preamble
  given Conversion[String,Try[JsValue]] = x => Success(JsStr(x))
  given Conversion[Int,Try[JsValue]] = x => Success(JsInt(x))
  given Conversion[Long,Try[JsValue]] = x => Success(JsLong(x))
  given Conversion[Double,Try[JsValue]] = x => Success(JsDouble(x))
  given Conversion[Boolean,Try[JsValue]] = x => Success(JsBool(x))
  given Conversion[BigInt,Try[JsValue]] = x => Success(JsBigInt(x))
  given Conversion[BigDecimal,Try[JsValue]] = x => Success(JsBigDec(x))
  given Conversion[JsArray,Try[JsValue]] = Success(_)
  given Conversion[JsObj,Try[JsValue]] = Success(_)
  given Conversion[JsNull.type,Try[JsValue]] = Success(_)

