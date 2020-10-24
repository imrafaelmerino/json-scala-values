package json.value.scalatest_migrated
import scala.language.implicitConversions
import org.junit.{Assert, Test}
import json.value.Preamble.{given}
import json.value.spec.Preamble.{given}
import json.value.spec.{JsArraySpec, JsObjSpec, JsSpecs}
import json.value.{JsArray, JsNothing, JsNull, JsObj}

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
