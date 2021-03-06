package json.value.spec

import json.value.{JsArray, JsNothing, JsNull, JsObj, JsValue, spec}
import json.value.spec.JsNumberSpecs.{decimalSuchThat, intSuchThat, integralSuchThat, longSuchThat}
import json.value.spec.JsStrSpecs.strSuchThat
import scala.language.implicitConversions

object Preamble
{
  implicit def strSpec2KeySpec(p: (String, JsSpec)): (NamedKey, JsSpec) = (NamedKey(p._1), p._2)

  implicit def strStr2KeySpec(p: (String, String)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsStrSuchThat(s => if (s == p._2) Valid else Invalid(s"$s not equals to $p._2")))

  implicit def strInt2KeySpec(p: (String, Int)): (NamedKey, JsSpec) =
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

  implicit def strNull2KeySpec(p: (String, JsNull.type)): (NamedKey, JsSpec) =
    (NamedKey(p._1), spec.IsValueSuchThat((value: JsValue) => if (value.isNull) Valid else Invalid("not null")))

  implicit def strNothing2KeySpec(p: (String, JsNothing.type)): (NamedKey, JsSpec) =
    (NamedKey(p._1), spec.IsValueSuchThat((value: JsValue) => if (value.isNothing) Valid else Invalid("exists json.value")))

  implicit def str2Spec(cons: String): JsSpec =
    strSuchThat(s => if (s == cons) Valid else Invalid(s"$s not equals to $cons"))

  implicit def int2Spec(cons: Int): JsSpec =
    intSuchThat(s => if (s == cons) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def long2Spec(cons: Long): JsSpec =
    longSuchThat(s => if (s == cons) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def bigInt2Spec(cons: BigInt): JsSpec =
    integralSuchThat((s: BigInt) => if (s == cons) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def bigDec2Spec(cons: BigDecimal): JsSpec =
    decimalSuchThat((s: BigDecimal) => if (s == cons) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def double2Spec(cons: Double): JsSpec =
    decimalSuchThat((s: BigDecimal) => if (s == BigDecimal(cons)) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def obj2Spec(cons: JsObj): JsSpec = IsObjSuchThat((s: JsObj) => if (s == cons) Valid else Invalid(s"$s is not equals to $cons"))

  implicit def arr2Spec(cons: JsArray): JsSpec =
    IsArrayOfValueSuchThat((a: JsArray) => if (a == cons) Valid else Invalid(s"$a is not equals to $cons"))

  implicit def boolean2Spec(cons: Boolean): JsSpec = if (cons) JsBoolSpecs.isTrue() else JsBoolSpecs.isFalse()
}
