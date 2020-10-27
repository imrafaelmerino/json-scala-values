package json.value

import json.value.Preamble.{_, given _}
import json.value.spec.Preamble.{_, given _}
import json.value.spec.{JsArraySpec, JsObjSpec, JsSpecs}
import json.value.{JsArray, JsNothing, JsNull, JsObj}
import org.junit.{Assert, Test}

import scala.language.implicitConversions

class JsSpecImplicitsSpec
{

  @Test
  def test_primitive2Spec_implicits_should_turn_primitive_types_into_JsValueSpec_in(): Unit =
  {
    Assert.assertTrue(JsObj.empty.validate(JsObjSpec("a" -> "hi",
                                                 "b" -> 1,
                                                 "c" -> 1L,
                                                 "d" -> JsArraySpec(1.2,
                                                                    true,
                                                                    false
                                                                    ),
                                                 "e" -> JsObj.empty,
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
