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

      "j" -> IsMapOfStr(k => if k.isEmpty then "key empty" else true,
                        v => if v.isEmpty then "val empty" else true)
    )


    val expected = LazyList(

      (JsPath.root / "j" / "",Invalid("",SpecError("val empty"))),
      (JsPath.root / "j" / "",Invalid("",SpecError("key empty")))

    )

    spec.validateAll(
      JsObj(
            "j" -> JsObj("" -> JsStr(""))
           )
                     ) should be(expected)

  }


}