package json.value

import json.value.Preamble.{_, given _}
import json.value.{JsArray, JsNull, JsObj}
import org.junit.{Assert, Test}

import scala.language.implicitConversions

class JsObjImplicitsSpec
{

  @Test
  def test_implicits_should_turn_pairs_into_pairs(): Unit =
  {

    Assert.assertTrue(JsObj("a" -> 1,
                            "b" -> 1.2,
                            "c" -> true,
                            "e" -> BigDecimal(1.5),
                            "f" -> BigInt(10),
                            "g" -> JsNull,
                            "h" -> JsObj.empty,
                            "i" -> JsArray.empty,
                            "j" -> "a",
                            "k" -> 10L
                            ).size > 0
                      )

    Assert.assertTrue(JsObj(("a", 1),
                            ("b", 1.2),
                            ("c", true),
                            ("e", BigDecimal(1.5)),
                            ("f", BigInt(10)),
                            ("g", JsNull),
                            ("h", JsObj.empty),
                            ("i", JsArray.empty),
                            ("j", "a"),
                            ("k" -> 10L)
                            ).size > 0
                      )
  }

}
