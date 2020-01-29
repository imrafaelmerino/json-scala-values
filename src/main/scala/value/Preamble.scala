package value

import value.JsPath.empty
import value.spec.JsNumberSpecs._
import value.spec.{Invalid, IsArrayOfStrSuchThat, IsArrayOfValueSuchThat, IsDecimalSuchThat, IsIntSuchThat, IsIntegralSuchThat, IsLongSuchThat, IsObjSuchThat, IsStrSuchThat, JsBoolSpecs, JsSpec, NamedKey, Valid}
import value.spec.JsStrSpecs._

import scala.language.implicitConversions

/**
 * singleton with all the implicit conversions of the library. It must be always imported in order to be
 * more concise and idiomatic defining Jsons, specs and JsPath.
 */
object Preamble
{
  implicit def strSpec2KeySpec(p: (String, JsSpec)): (NamedKey, JsSpec) = (NamedKey(p._1), p._2)

  implicit def strStr2KeySpec(p: (String, String)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsStrSuchThat(s => if (s == p._2) Valid else Invalid(s"$s not equals to $p._2")))

  implicit def strInt2KeySpec(p : (String, Int)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsIntSuchThat(s => if (s == p._2) Valid else Invalid(s"$s is not equals to $p._2")))

  implicit def strLong2KeySpec(p: (String, Long)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsLongSuchThat(s => if (s == p._2) Valid else Invalid(s"$s is not equals to $p._2")))

  implicit def strBigInt2KeySpec(p: (String, BigInt)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsIntegralSuchThat((s: BigInt) => if (s == p._2) Valid else Invalid(s"$s is not equals to $p._2")))

  implicit def strBigDec2KeySpec(p: (String, BigDecimal)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsDecimalSuchThat((s: BigDecimal) => if (s == p._2) Valid else Invalid(s"$s is not equals to $p._2")))

  implicit def strDouble2KeySpec(p: (String, Double)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsDecimalSuchThat((s: BigDecimal) => if (s == BigDecimal(p._2)) Valid else Invalid(s"$s is not equals to $p._2")))

  implicit def strBoolean2KeySpec(p: (String, Boolean)): (NamedKey, JsSpec) =
    (NamedKey(p._1), if (p._2) JsBoolSpecs.isTrue() else JsBoolSpecs.isFalse())

  implicit def strJsObj2KeySpec(p: (String, JsObj)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsObjSuchThat((s: JsObj) => if (s == p._2) Valid else Invalid(s"$s is not equals to $p._2")))

  implicit def strJsArr2KeySpec(p: (String, JsArray)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsArrayOfStrSuchThat((a: JsArray) => if (a == p._2) Valid else Invalid(s"$a is not equals to $p._2")))

  implicit def strNull2KeySpec(p : (String, JsNull.type)): (NamedKey, JsSpec) =
    (NamedKey(p._1), spec.IsValueSuchThat((value: JsValue) => if (value.isNull) Valid else Invalid("not null")))

  implicit def strNothing2KeySpec(p: (String, JsNothing.type)): (NamedKey, JsSpec) =
    (NamedKey(p._1), spec.IsValueSuchThat((value: JsValue) => if (value.isNothing) Valid else Invalid("exists value")))

  implicit def str2Spec(cons: String): JsSpec =
    strSuchThat(s => if (s == cons) Valid else Invalid(s"$s not equals to $cons"))

  implicit def int2Spec(cons: Int): JsSpec =
    intSuchThat(s => if (s == cons) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def long2Spec(cons: Long): JsSpec =
    longSuchThat(s => if (s == cons) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def bigInt2Spec(cons: BigInt): JsSpec =
    integralSuchThat((s      : BigInt) => if (s == cons) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def bigDec2Spec(cons: BigDecimal): JsSpec =
    decimalSuchThat((s      : BigDecimal) => if (s == cons) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def double2Spec(cons: Double): JsSpec =
    decimalSuchThat((s: BigDecimal) => if (s == BigDecimal(cons)) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def obj2Spec(cons: JsObj): JsSpec = IsObjSuchThat((s:JsObj) => if(s == cons) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def arr2Spec(cons: JsArray): JsSpec =
    IsArrayOfValueSuchThat((a: JsArray) => if (a == cons) Valid else Invalid(s"$a is not equals to $cons"))

  implicit def boolean2Spec(cons: Boolean): JsSpec = if (cons) JsBoolSpecs.isTrue() else JsBoolSpecs.isFalse()

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
