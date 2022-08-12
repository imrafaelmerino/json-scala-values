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


  "or operator" should "returns no error if a schema is valid" in {
    val a = JsObjSpec("a" -> IsInt, "b" -> IsStr)

    val b = JsObjSpec("c" -> IsInt, "d" -> IsStr)

    val c = a.or(b)

    c.validateAll(JsObj("a" -> 1, "b" -> "hi")) should be(LazyList.empty)

    c.validateAll(JsObj("c" -> 1, "d" -> "hi")) should be(LazyList.empty)


  }

  "or operator" should "returns errors from the last spec if no schema is valid" in {
    val a = JsObjSpec("a" -> IsInt, "b" -> IsStr)

    val b = JsObjSpec("c" -> IsStr, "d" -> IsInt)

    val c = a.or(b)

    val expected = LazyList(
      (JsPath.root / "e", Invalid(1, SPEC_FOR_VALUE_NOT_DEFINED)), 
      (JsPath.root / "f", Invalid("hi", SPEC_FOR_VALUE_NOT_DEFINED)),
      (JsPath.root / "c", Invalid(JsNothing,KEY_REQUIRED)), 
      (JsPath.root / "d", Invalid(JsNothing,KEY_REQUIRED))
    )

    c.validateAll(JsObj("e" -> 1, "f" -> "hi")) should be(expected)

    val d = a.or(b.lenient)

    val xs = LazyList((JsPath.root / "c", Invalid(JsNothing, KEY_REQUIRED)), (JsPath.root / "d", Invalid(JsNothing, KEY_REQUIRED)))
    d.validateAll(JsObj("e" -> 1, "f" -> "hi")) should be(xs)
    
  }

  "custom messages" should "return all the errors" in {

    val spec = JsObjSpec(
      "a" -> IsInt(n => if n > 0 then true else "lower than zero"),
      "b" -> IsLong(n => if n > 0 then true else "lower than zero"),
      "c" -> IsStr(s => if s.nonEmpty then true else "empty string"),
      "d" -> IsInstant(s => if s.isAfter(Instant.EPOCH) then true else "before epoch"),
      "e" -> IsDec(s => if s.isValidLong then true else "not valid long")
    )

    val expected = LazyList(
      (JsPath.root / "a", Invalid(-1, SpecError("lower than zero"))),
      (JsPath.root / "b", Invalid(-1, SpecError("lower than zero"))),
      (JsPath.root / "c", Invalid("", SpecError("empty string"))),
      (JsPath.root / "d", Invalid(Instant.EPOCH, SpecError("before epoch"))),
      (JsPath.root / "e", Invalid(BigDecimal(1.5), SpecError("not valid long")))
    )

    spec.validateAll(JsObj("a" -> -1, "b" -> -1, "c" -> "","d" -> Instant.EPOCH, "e" -> BigDecimal(1.5))) should be(expected)
  }


}