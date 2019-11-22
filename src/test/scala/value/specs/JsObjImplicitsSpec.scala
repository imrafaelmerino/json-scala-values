package value.specs

import org.scalatest.FlatSpec
import value.Implicits.{bigDec2JsValue, bigInt2JsValue, bool2JsValue, double2JsValue, int2JsValue, keyBigDec2JsPair, keyBigInt2JsPair, keyBool2JsPair, keyDouble2JsPair, keyInt2JsPair, keyJsValueToJsPair, keyLong2JsPair, keyNull2JsPair, keyStr2JsPair, long2JsValue, str2JsPath, str2JsValue}
import value.{JsArray, JsNull, JsObj}
import value.spec.JsValueSpec._

class JsObjImplicitsSpec extends FlatSpec
{

  "implicits" should "turn (key,obj) pairs into (JsPath,JsValue) pairs" in
  {

    assert(JsObj("a" -> 1,
                 "b" -> 1.2,
                 "c" -> true,
                 "e" -> BigDecimal(1.5),
                 "f" -> BigInt(10),
                 "g" -> JsNull,
                 "h" -> JsObj(),
                 "i" -> JsArray(),
                 "j" -> "a",
                 "k" -> 10L
                 ).size > 0
           )

    assert(JsObj(("a", 1),
                 ("b", 1.2),
                 ("c", true),
                 ("e", BigDecimal(1.5)),
                 ("f", BigInt(10)),
                 ("g", JsNull),
                 ("h", JsObj()),
                 ("i", JsArray()),
                 ("j", "a"),
                 ("k" -> 10L)
                 ).size > 0
           )
  }

}
