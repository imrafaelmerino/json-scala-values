package value.specs

import org.scalatest.FlatSpec
import value.{JsArray, JsObj}
import value.Preamble._
import value.exc.Preamble._
import value.exc.JsObjTry

import scala.language.implicitConversions
import scala.util.Try

class JsObjTrySpec extends FlatSpec
{

  "value implicit conversion" should "kick in" in
  {
    val tryObj: Try[JsObj] = JsObjTry("a" -> 1,
                                      "b" -> "hi",
                                      "c" -> JsObj.empty,
                                      "e" -> false,
                                      "f" -> 1.5,
                                      "g" -> 10L,
                                      "h" -> JsArray.empty,
                                      "i" -> BigInt(10),
                                      "j" -> BigDecimal(1.5)
                                      )


    assert(
      tryObj.get ==
      JsObj("a" -> 1,
            "b" -> "hi",
            "c" -> JsObj.empty,
            "e" -> false,
            "f" -> 1.5,
            "g" -> 10L,
            "h" -> JsArray.empty,
            "i" -> BigInt(10),
            "j" -> BigDecimal(1.5)
            )
      )
  }
  "json obj try" should "return a try" in
  {
    val tryObj: Try[JsObj] = JsObjTry("a" -> Try({1}),
                                      "b" -> Try({"hi"}),
                                      "c" -> Try({JsObj.empty}),
                                      "e" -> Try({false}),
                                      "f" -> Try({1.5}),
                                      "g" -> Try({10L}),
                                      "h" -> Try({JsArray.empty}),
                                      "i" -> Try({BigInt(10)}),
                                      "j" -> Try({BigDecimal(1.5)})
                                      )


    assert(
      tryObj.get ==
      JsObj("a" -> 1,
            "b" -> "hi",
            "c" -> JsObj.empty,
            "e" -> false,
            "f" -> 1.5,
            "g" -> 10L,
            "h" -> JsArray.empty,
            "i" -> BigInt(10),
            "j" -> BigDecimal(1.5)
            )
      )
  }


}