package json.value.spec

import json.value.{JsArray, JsNothing, JsNull, JsObj, JsValue, spec}
import json.value.spec.JsNumberSpecs.{decimalSuchThat, intSuchThat, integralSuchThat, longSuchThat}
import json.value.spec.JsStrSpecs.strSuchThat
import scala.language.implicitConversions

object Preamble

  given Conversion[(String,JsSpec),(NamedKey,JsSpec)] = p =>(NamedKey(p._1), p._2)

  given Conversion[(String,String),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), IsStrSuchThat(s => if s == p._2 then Valid
                                             else Invalid(s"$s not equals to $p._2")))

  given Conversion[(String,Int),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), IsIntSuchThat(s => if s == p._2 then Valid
                                             else Invalid(s"$s is not equals to $p._2")))

  given Conversion[(String,Long),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), IsLongSuchThat(s => if s == p._2 then Valid
                                              else Invalid(s"$s is not equals to $p._2")))

  given Conversion[(String,BigInt),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), IsIntegralSuchThat(s => if s == p._2 then Valid
                                                  else Invalid(s"$s is not equals to $p._2")))

  given Conversion[(String,BigDecimal),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), IsDecimalSuchThat(s => if s == p._2 then Valid
                                                 else Invalid(s"$s is not equals to $p._2")) )

  given Conversion[(String,Double),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), IsDecimalSuchThat(s => if s == BigDecimal(p._2) then Valid
                                                 else Invalid(s"$s is not equals to $p._2")))

  given Conversion[(String,Boolean),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), if p._2 then JsBoolSpecs.isTrue()
                          else JsBoolSpecs.isFalse())

  given Conversion[(String,JsObj),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), IsObjSuchThat(s=> if s == p._2 then Valid
                                            else Invalid(s"$s is not equals to $p._2")))

  given Conversion[(String,JsArray),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), IsArrayOfStrSuchThat(a => if a == p._2 then Valid
                                                    else Invalid(s"$a is not equals to $p._2")))

  given Conversion[(String,JsNull.type),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), spec.IsValueSuchThat(value => if value.isNull then Valid
                                                        else Invalid("not null")))

  given isNothing:Conversion[(String,JsNothing.type),(NamedKey,JsSpec)] =
    p => (NamedKey(p._1), spec.IsValueSuchThat(value => if value.isNothing then Valid
                                                        else Invalid("exists json.value")))

  given Conversion[String,JsSpec] =
    cons => strSuchThat(s => if s == cons then Valid
                             else Invalid(s"$s not equals to $cons"))

  given Conversion[Int,JsSpec] =
    cons => intSuchThat(s => if s == cons then Valid
                             else Invalid(s"$s is not equals to $cons"))

  given Conversion[Long,JsSpec] =
    cons => longSuchThat(s => if s == cons then Valid
                              else Invalid(s"$s is not equals to $cons"))

  given Conversion[BigInt,JsSpec] =
    cons => integralSuchThat(s => if s == cons then Valid
                                  else Invalid(s"$s is not equals to $cons"))

  given Conversion[BigDecimal,JsSpec] =
    cons => decimalSuchThat(s => if s == cons then Valid
                                 else Invalid(s"$s is not equals to $cons"))

  given Conversion[Double,JsSpec] =
    cons => decimalSuchThat(s => if s == BigDecimal(cons) then Valid
                                 else Invalid(s"$s is not equals to $cons"))

  given Conversion[JsObj,JsSpec] =
    cons => IsObjSuchThat(s => if s == cons then Valid
                               else Invalid(s"$s is not equals to $cons"))

  given Conversion[JsArray,JsSpec] =
    cons => IsArrayOfValueSuchThat(a => if a == cons then Valid
                                        else Invalid(s"$a is not equals to $cons"))

  given Conversion[Boolean,JsSpec] =
    cons => if cons then JsBoolSpecs.isTrue()
            else JsBoolSpecs.isFalse()
