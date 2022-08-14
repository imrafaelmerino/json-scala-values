package tests
import json.value.spec.*
import json.value.*
import json.value.spec.SpecError.*
import json.value.Conversions.given
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import java.time.Instant
import scala.language.implicitConversions
class JsObjSpecTests extends AnyFlatSpec with should.Matchers {



  "custom messages" should "return all the errors" in {

    val spec = JsObjSpec(
      "a" -> IsInt(n => if n > 0 then true else "lower than zero"),
      "b" -> IsLong(n => if n > 0 then true else "lower than zero"),
      "c" -> IsStr(s => if s.nonEmpty then true else "empty string"),
      "d" -> IsInstant(s => if s.isAfter(Instant.EPOCH) then true else "before epoch"),
      "e" -> IsDec(s => if s.isValidLong then true else "not valid long"),
      "f" -> IsBigInt(s => if s.isValidLong then true else "not valid long"),
      "g" -> IsBool,
      "h" -> IsJsObj(s => if s.isEmpty then "empty" else true),
      "i" -> IsArray(s => if s.isEmpty then "empty" else true),
      "j" -> IsMapOfStr(k => if k.isEmpty then "val empty" else true,
                        v => if v.isEmpty then "key empty" else true),
      "k" -> IsMapOfObj(v => if v.isEmpty then "val empty" else true,
                        k => if k.isEmpty then "key empty" else true)
    )


    val expected = LazyList(
      (JsPath.root / "a", Invalid(-1, SpecError("lower than zero"))),
      (JsPath.root / "b", Invalid(-1, SpecError("lower than zero"))),
      (JsPath.root / "c", Invalid("", SpecError("empty string"))),
      (JsPath.root / "d", Invalid(Instant.EPOCH, SpecError("before epoch"))),
      (JsPath.root / "e", Invalid(BigDecimal(1.5), SpecError("not valid long"))),
      (JsPath.root / "f", Invalid(BigInt("1111111111111111111111111111111111111111111111111"), SpecError("not valid long"))),
      (JsPath.root / "g", Invalid("a", SpecError.BOOLEAN_EXPECTED)),
      (JsPath.root / "h", Invalid(JsObj.empty, SpecError("empty"))),
      (JsPath.root / "i", Invalid(JsArray.empty, SpecError("empty"))),
      (JsPath.root / "j" / "",Invalid("",SpecError("val empty"))),
      (JsPath.root / "j" / "",Invalid("",SpecError("key empty"))),
      (JsPath.root / "k" / "", Invalid(JsObj.empty, SpecError("val empty"))),
      (JsPath.root / "k" / "", Invalid("", SpecError("key empty")))

    )

    spec.validateAll(
      JsObj("a" -> -1,
            "b" -> -1,
            "c" -> "",
            "d" -> Instant.EPOCH,
            "e" -> BigDecimal(1.5),
            "f" -> BigInt("1111111111111111111111111111111111111111111111111"),
            "g" -> "a",
            "h" -> JsObj.empty,
            "i" -> JsArray.empty,
            "j" -> JsObj("" -> JsStr("")),
            "k" -> JsObj("" -> JsObj.empty)
           )
                     ) should be(expected)

  }


}