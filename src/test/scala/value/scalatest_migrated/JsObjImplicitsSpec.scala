package value.scalatest_migrated
import scala.language.implicitConversions

import org.junit.{Assert, Test}
import value.Preamble.{bigDec2JsValue, bigInt2JsValue, bool2JsValue, double2JsValue, int2JsValue, keyBigDec2JsPair, keyBigInt2JsPair, keyBool2JsPair, keyDouble2JsPair, keyInt2JsPair, keyJsValue2JsPair, keyLong2JsPair, keyStr2JsPair, str2JsPath, str2JsValue}
import value.{JsArray, JsNull, JsObj}

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
