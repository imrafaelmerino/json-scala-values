package json.value.specs

import org.scalatest.FlatSpec
import json.value.Preamble._
import json.value.spec.JsSpecs.any
import json.value.spec.Preamble._
import json.value.spec.{JsArraySpec, JsObjSpec, JsSpecs}
import json.value.{JsArray, JsNothing, JsNull, JsObj}

class JsSpecImplicitsSpec extends FlatSpec
{



  "primitive2Spec implicits" should "turn primitive types into JsValueSpec" in
  {
    assert(JsObj().validate(JsObjSpec("a" -> "hi",
                                      "b" -> 1,
                                      "c" -> 1L,
                                      "d" -> JsArraySpec(1.2,
                                                         true,
                                                         false
                                                         ),
                                      "e" -> JsObj(),
                                      "f" -> JsArray.empty,
                                      "g" -> JsObjSpec("h" -> BigInt(1),
                                                       "i" -> BigDecimal(3.2),
                                                       "j" -> JsNull
                                                       ),
                                      "h" -> JsNothing,
                                      json.value.spec.* -> JsSpecs.any
                                      )
                            ).nonEmpty
           )
  }

}
