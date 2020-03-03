package value.spec

import value.{JsArray, JsNothing, JsNull, JsObj, JsValue, spec}
import value.spec.JsNumberSpecs.{decimalSuchThat, intSuchThat, integralSuchThat, longSuchThat}
import value.spec.JsStrSpecs.strSuchThat
import scala.language.implicitConversions

object Preamble
  implicit def strSpec2KeySpec(p: (String, JsSpec)): (NamedKey, JsSpec) = (NamedKey(p._1), p._2)

  implicit def strStr2KeySpec(p: (String, String)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsStrSuchThat(s => if s == p._2
                                        then Valid
                                        else Invalid(s"$s not equals to $p._2")))

  implicit def strInt2KeySpec(p: (String, Int)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsIntSuchThat(s => if s == p._2
                                        then Valid
                                        else Invalid(s"$s is not equals to $p._2")))

  implicit def strLong2KeySpec(p: (String, Long)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsLongSuchThat(s => if s == p._2
                                         then Valid
                                         else Invalid(s"$s is not equals to $p._2")))

  implicit def strBigInt2KeySpec(p: (String, BigInt)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsIntegralSuchThat(s => if s == p._2
                                             then Valid
                                             else Invalid(s"$s is not equals to $p._2")))

  implicit def strBigDec2KeySpec(p: (String, BigDecimal)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsDecimalSuchThat(s => if s == p._2
                                            then Valid
                                            else Invalid(s"$s is not equals to $p._2")) )

  implicit def strDouble2KeySpec(p: (String, Double)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsDecimalSuchThat(s => if s == BigDecimal(p._2)
                                            then Valid
                                            else Invalid(s"$s is not equals to $p._2")))

  implicit def strBoolean2KeySpec(p: (String, Boolean)): (NamedKey, JsSpec) =
    (NamedKey(p._1), if p._2
                     then JsBoolSpecs.isTrue()
                     else JsBoolSpecs.isFalse())

  implicit def strJsObj2KeySpec(p: (String, JsObj)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsObjSuchThat(s=> if s == p._2
                                       then Valid
                                       else Invalid(s"$s is not equals to $p._2")))

  implicit def strJsArr2KeySpec(p: (String, JsArray)): (NamedKey, JsSpec) =
    (NamedKey(p._1), IsArrayOfStrSuchThat(a => if a == p._2
                                               then Valid
                                               else Invalid(s"$a is not equals to $p._2")))

  implicit def strNull2KeySpec(p: (String, JsNull.type)): (NamedKey, JsSpec) =
    (NamedKey(p._1), spec.IsValueSuchThat(value => if value.isNull
                                                   then Valid
                                                   else Invalid("not null")))

  implicit def strNothing2KeySpec(p: (String, JsNothing.type)): (NamedKey, JsSpec) =
    (NamedKey(p._1), spec.IsValueSuchThat(value => if value.isNothing
                                                   then Valid
                                                   else Invalid("exists value")))

  implicit def str2Spec(cons: String): JsSpec =
    strSuchThat(s => if s == cons
                     then Valid
                     else Invalid(s"$s not equals to $cons"))

  implicit def int2Spec(cons: Int): JsSpec =
    intSuchThat(s => if s == cons
                     then Valid
                     else Invalid(s"$s is not equals to $cons"))

  implicit def long2Spec(cons: Long): JsSpec =
    longSuchThat(s => if s == cons
                      then Valid
                      else Invalid(s"$s is not equals to $cons"))

  implicit def bigInt2Spec(cons: BigInt): JsSpec =
    integralSuchThat(s => if s == cons
                          then Valid
                          else Invalid(s"$s is not equals to $cons"))

  implicit def bigDec2Spec(cons: BigDecimal): JsSpec =
    decimalSuchThat(s => if s == cons
                         then Valid
                         else Invalid(s"$s is not equals to $cons"))

  implicit def double2Spec(cons: Double): JsSpec =
    decimalSuchThat(s => if s == BigDecimal(cons)
                         then Valid
                         else Invalid(s"$s is not equals to $cons"))

  implicit def obj2Spec(cons: JsObj): JsSpec =
    IsObjSuchThat(s => if s == cons
                       then Valid
                       else Invalid(s"$s is not equals to $cons"))

  implicit def arr2Spec(cons: JsArray): JsSpec =
    IsArrayOfValueSuchThat(a => if a == cons
                                then Valid
                                else Invalid(s"$a is not equals to $cons"))

  implicit def boolean2Spec(cons: Boolean): JsSpec = if cons
                                                     then JsBoolSpecs.isTrue()
                                                     else JsBoolSpecs.isFalse()
