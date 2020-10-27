package json.value

import org.junit.Assert._
import org.junit.Test
import json.value.Preamble.{given _}
import json.value.exc.Preamble.{given _}
import json.value.exc.JsArrayTry

import scala.language.implicitConversions
import scala.util.Try

class JsArrayTrySpec
{

  @Test
  def implicits_conversions(): Unit =
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

    assertEquals("future is completed",
                 tryArray.get,
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
