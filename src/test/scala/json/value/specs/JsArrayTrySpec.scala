package json.value.specs

import org.scalatest.FlatSpec
import json.value.{JsArray, JsNull, JsObj}
import json.value.Preamble._
import json.value.exc.Preamble._
import json.value.exc.JsArrayTry

import scala.language.implicitConversions
import scala.util.Try

class JsArrayTrySpec extends FlatSpec
{
"try implicit conversions" should "kick in" in
  {
    val tryArray: Try[JsArray] = JsArrayTry(1,
                                            true,
                                            1.5,
                                            false,
                                            10L,
                                            BigInt(1),
                                            BigDecimal(1.5),
                                            JsObj.empty,
                                            JsArray.empty,
                                            "a",
                                            JsNull
                                            )

    assert(
                 tryArray.get ==
                 JsArray(1,
                         true,
                         1.5,
                         false,
                         10L,
                         BigInt(1),
                         BigDecimal(1.5),
                         JsObj.empty,
                         JsArray.empty,
                         "a",
                         JsNull
                         )
                 )
  }


}
