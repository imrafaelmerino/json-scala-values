package value.specs

import org.scalatest.FlatSpec
import value.{JsArray, JsNull, JsObj}
import value.Preamble._
import value.exc.Preamble._
import value.exc.JsArrayTry

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
