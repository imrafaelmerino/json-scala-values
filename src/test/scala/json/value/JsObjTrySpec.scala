package json.value

import org.junit.Assert._
import org.junit.Test
import json.value.exc.Preamble.{given _}
import json.value.Preamble.{given _}
import json.value.exc.JsObjTry

import scala.language.implicitConversions
import scala.util.Try
class JsObjTrySpec
{


  @Test
  def implicits_conversions(): Unit =
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


    assertEquals("future is completed",
                 tryObj.get,
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

  @Test
  def implicits_try_conversions(): Unit =
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


    assertEquals("future is completed",
                 tryObj.get,
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
