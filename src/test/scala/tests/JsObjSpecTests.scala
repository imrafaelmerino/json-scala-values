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

      "k" -> IsMapOfObj(v => if v.isEmpty then "val empty" else true,
                        k => if k.isEmpty then "key empty" else true)
    )


    val expected = LazyList(
      (JsPath.root / "k" / "", Invalid(JsObj.empty, SpecError("val empty"))),
      (JsPath.root / "k" / "", Invalid("", SpecError("key empty")))
    )

    spec.validateAll(JsObj("k" -> JsObj("" -> JsObj.empty))) should be(expected)

  }


}