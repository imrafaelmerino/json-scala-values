package tests
import json.value.spec.*
import json.value.*
import json.value.spec.SpecError.*
import json.value.Conversions.given
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
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


}