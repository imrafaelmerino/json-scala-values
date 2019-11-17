package value.specs

import org.scalatest.FlatSpec
import value.{JsArray, JsNull, JsObj}
import value.Implicits.{bigDec2JsValue, bigInt2JsValue, bool2JsValue, double2JsValue, int2JsValue, long2JsValue, str2JsValue}

class JsArrayImplicitsSpec extends FlatSpec
{
  "implicits" should "turn objects into JsValues" in
  {

    assert(JsArray(1,
                   1.2,
                   true,
                   BigDecimal(1.5),
                   BigInt(10),
                   JsNull,
                   JsObj(),
                   JsArray(),
                   "a",
                   10L
                   ).size > 0
           )

//    assert(JsArray((0, 1),
    //                   (1, 1.2),
    //                   (2, true),
    //                   (3, BigDecimal(1.5)),
    //                   (4, BigInt(10)),
    //                   (5, JsNull),
    //                   (6, JsObj()),
    //                   (7, JsArray()),
    //                   (8, "a"),
    //                   (9, 10L)
    //                   ).size > 0
    //           )

  }
}
