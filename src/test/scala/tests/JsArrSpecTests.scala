package tests

import json.value.*
import json.value.Conversions.given
import json.value.spec.*
import json.value.spec.SpecError.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.time.Instant
import scala.language.implicitConversions

class JsArrSpecTests extends AnyFlatSpec with should.Matchers {
  val mapSpecs = IsTuple(
    IsMapOfStr(keySuchThat= (k:String) => k.nonEmpty),
    IsMapOfObj(keySuchThat= (k:String) => k.nonEmpty),
    IsMapOfArr(keySuchThat= (k:String) => k.nonEmpty),
    IsMapOfInt(keySuchThat= (k:String) => k.nonEmpty),
    IsMapOfLong(keySuchThat= (k:String) => k.nonEmpty),
    IsMapOfInstant(keySuchThat= (k:String) => k.nonEmpty),
    IsMapOfIntegral(keySuchThat= (k:String) => k.nonEmpty),
    IsMapOfNumber(keySuchThat= (k:String) => k.nonEmpty),
    IsMapOfBool(keySuchThat= (k:String) => k.nonEmpty)
  )

  val mapCustomMessageSpecs = IsTuple(
    IsMapOfStr(v => if v.isEmpty then "val empty" else true, k => if k.isEmpty then "key empty" else true),
    IsMapOfObj(v => if v.isEmpty then "val empty" else true, k => if k.isEmpty then "key empty" else true),
    IsMapOfArr(v => if v.isEmpty then "val empty" else true, k => if k.isEmpty then "key empty" else true),
    IsMapOfInt(v => if v < 0 then "lower than zero" else true, k => if k.isEmpty then "key empty" else true),
    IsMapOfLong(v => if v < 0 then "lower than zero" else true, k => if k.isEmpty then "key empty" else true),
    IsMapOfInstant(v => if v.isAfter(Instant.EPOCH) then "after epoch" else true, k => if k.isEmpty then "key empty" else true),
    IsMapOfIntegral(v => if v.isValidLong then "valid long" else true, k => if k.isEmpty then "key empty" else true),
    IsMapOfNumber(v => if v.isValidInt then "valid int" else true, k => if k.isEmpty then "key empty" else true)
  )

  val mapDefaultMessageSpecs = IsTuple(
    IsMapOfStr(v => v.nonEmpty , k => k.nonEmpty),
    IsMapOfObj(v => v.nonEmpty, k => k.nonEmpty),
    IsMapOfArr(v => v.nonEmpty, k => k.nonEmpty),
    IsMapOfInt(v => if v < 0 then false else true, k => k.nonEmpty),
    IsMapOfLong(v => if v < 0 then false else true, k =>k.nonEmpty),
    IsMapOfInstant(v => if v.isAfter(Instant.EPOCH) then false else true, k => k.nonEmpty),
    IsMapOfIntegral(v => if v.isValidLong then false else true, k => k.nonEmpty),
    IsMapOfNumber(v => if v.isValidInt then false else true, k => k.nonEmpty)
  )

  "validateAll" should "return all the error" in {

    val spec = IsTuple(
      IsInt(n => if n > 0 then true else "lower than zero"),
      IsLong(n => if n > 0 then true else "lower than zero"),
      IsStr(s => if s.nonEmpty then true else "empty string"),
      IsInstant(s => if s.isAfter(Instant.EPOCH) then true else "before epoch"),
      IsNumber(s => if s.isValidLong then true else "not valid long"),
      IsIntegral(s => if s.isValidLong then true else "not valid long"),
      IsBool,
      IsJsObj(s => if s.isEmpty then "empty" else true),
      IsArray(s => if s.isEmpty then "empty" else true),
    ).appendedAll(mapCustomMessageSpecs)
     .appendedAll(mapDefaultMessageSpecs)
     .appendedAll(mapSpecs)


    val expected = LazyList(
      (0, Invalid(-1, SpecError("lower than zero"))),
      (1, Invalid(-1, SpecError("lower than zero"))),
      (2, Invalid("", SpecError("empty string"))),
      (3, Invalid(Instant.EPOCH, SpecError("before epoch"))),
      (4, Invalid(BigDecimal(1.5), SpecError("not valid long"))),
      (5, Invalid(BigInt("1111111111111111111111111111111111111111111111111"), SpecError("not valid long"))),
      (6, Invalid("a", SpecError.BOOLEAN_EXPECTED)),
      (7, Invalid(JsObj.empty, SpecError("empty"))),
      (8, Invalid(JsArray.empty, SpecError("empty"))),
      (9 / "",Invalid("",SpecError("val empty"))),
      (9 / "",Invalid("",SpecError("key empty"))),
      (10 / "", Invalid(JsObj.empty, SpecError("val empty"))),
      (10 / "", Invalid("", SpecError("key empty"))),
      (11 / "", Invalid(JsArray.empty, SpecError("val empty"))),
      (11 / "", Invalid("", SpecError("key empty"))),
      (12 / "", Invalid(-1, SpecError("lower than zero"))),
      (12 / "", Invalid("", SpecError("key empty"))) ,
      (13 / "", Invalid(-1, SpecError("lower than zero"))),
      (13 / "", Invalid("", SpecError("key empty"))),
      (14 / "", Invalid(Instant.MAX, SpecError("after epoch"))),
      (14 / "", Invalid("", SpecError("key empty"))),
      (15 / "", Invalid(Long.MaxValue, SpecError("valid long"))),
      (15 / "", Invalid("", SpecError("key empty"))),
      (16 / "", Invalid(Int.MaxValue, SpecError("valid int"))),
      (16 / "", Invalid("", SpecError("key empty"))),
      (17 / "", Invalid("", SpecError.STRING_CONDITION_FAILED)),
      (17 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (18 / "", Invalid(JsObj.empty, SpecError.OBJ_CONDITION_FAILED)),
      (18 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (19 / "", Invalid(JsArray.empty, SpecError.ARRAY_CONDITION_FAILED)),
      (19 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (20 / "", Invalid(-1, SpecError.INT_CONDITION_FAILED)),
      (20 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (21 / "", Invalid(-1, SpecError.LONG_CONDITION_FAILED)),
      (21 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (22 / "", Invalid(Instant.MAX, SpecError.INSTANT_CONDITION_FAILED)),
      (22 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (23 / "", Invalid(Long.MaxValue, SpecError.BIG_INTEGER_CONDITION_FAILED)),
      (23 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (24 / "", Invalid(Int.MaxValue, SpecError.DECIMAL_CONDITION_FAILED)),
      (24 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (25 / "", Invalid(0, SpecError.STRING_EXPECTED)),
      (25 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (26 / "", Invalid(0, SpecError.OBJ_EXPECTED)),
      (26 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (27 / "", Invalid(0, SpecError.ARRAY_EXPECTED)),
      (27 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (28 / "", Invalid("", SpecError.INT_EXPECTED)),
      (28 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (29 / "", Invalid("", SpecError.LONG_EXPECTED)),
      (29 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (30 / "", Invalid(0, SpecError.INSTANT_EXPECTED)),
      (30 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (31 / "", Invalid("", SpecError.BIG_INTEGER_EXPECTED)),
      (31 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (32 / "", Invalid("", SpecError.DECIMAL_EXPECTED)),
      (32 / "", Invalid("", SpecError.KEY_CONDITION_FAILED)),
      (33 / "", Invalid("", SpecError.BOOLEAN_EXPECTED)),
      (33 / "", Invalid("", SpecError.KEY_CONDITION_FAILED))
    )



    spec.validateAll(
      JsArray(-1,
              -1,
              "",
              Instant.EPOCH,
              BigDecimal(1.5),
              BigInt("1111111111111111111111111111111111111111111111111"),
              "a",
              JsObj.empty,
              JsArray.empty,
              JsObj("" -> JsStr("")),
              JsObj("" -> JsObj.empty),
              JsObj("" -> JsArray.empty),
              JsObj("" -> JsInt(-1)),
              JsObj("" -> JsInt(-1)),
              JsObj("" -> JsInstant(Instant.MAX)),
              JsObj("" -> JsLong(Long.MaxValue)),
              JsObj("" -> JsInt(Int.MaxValue)),
              JsObj("" -> JsStr("")),
              JsObj("" -> JsObj.empty),
              JsObj("" -> JsArray.empty),
              JsObj("" -> JsInt(-1)),
              JsObj("" -> JsInt(-1)),
              JsObj("" -> JsInstant(Instant.MAX)),
              JsObj("" -> JsLong(Long.MaxValue)),
              JsObj("" -> JsInt(Int.MaxValue)),
              JsObj("" -> JsInt(0)),
              JsObj("" -> JsInt(0)),
              JsObj("" -> JsInt(0)),
              JsObj("" -> JsStr("")),
              JsObj("" -> JsStr("")),
              JsObj("" -> JsInt(0)),
              JsObj("" -> JsStr("")),
              JsObj("" -> JsStr("")),
              JsObj("" -> JsStr("")),
           )
                     ) should be(expected)

  }

  "IsArrayOf.validate" should "return a valid or invalid result with the first error" in {

    val spec = IsArrayOf(IsStr)

    spec.validate(JsArray(1,1)) should be(Invalid(1,SpecError.STRING_EXPECTED))

    spec.validate(JsArray("1","2")) should be(Valid)

  }

  "IsArrayOf.validateAll" should "return a valid or invalid result with the all the errors" in {
    val spec = IsArrayOf(IsStr)

    spec.validateAll(JsArray(1, 1)) should be(LazyList((0,Invalid(1,STRING_EXPECTED)), (1,Invalid(1,STRING_EXPECTED))))


  }




}