package value.exc

import value.{JsArray, JsBigDec, JsBigInt, JsBool, JsDouble, JsInt, JsLong, JsNull, JsObj, JsStr, JsValue}
import scala.language.implicitConversions

import scala.util.{Success, Try}

object Preamble
  implicit def str2Try(p: String): Try[JsValue] = Success(JsStr(p))

  implicit def int2Try(p: Int): Try[JsValue] = Success(JsInt(p))

  implicit def long2Try(p: Long): Try[JsValue] = Success(JsLong(p))

  implicit def double2Try(p: Double): Try[JsValue] = Success(JsDouble(p))

  implicit def bigInt2Try(p: BigInt): Try[JsValue] = Success(JsBigInt(p))

  implicit def bigDec2Try(p: BigDecimal): Try[JsValue] = Success(JsBigDec(p))

  implicit def bool2Try(p: Boolean): Try[JsValue] = Success(JsBool(p))

  implicit def jsObj2Try(p: JsObj): Try[JsValue] = Success(p)

  implicit def jsArray2Try(p: JsArray): Try[JsValue] = Success(p)

  implicit def null2Try(p: JsNull.type): Try[JsValue] = Success(p)
