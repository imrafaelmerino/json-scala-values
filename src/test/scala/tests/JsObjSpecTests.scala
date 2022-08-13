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