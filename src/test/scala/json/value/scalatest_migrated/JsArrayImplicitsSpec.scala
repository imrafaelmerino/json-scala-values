package json.value.scalatest_migrated
import json.value.{JsArray, JsNull, JsObj}
import org.junit.Test
import json.value.Preamble.{given _}
import org.junit.Assert
import scala.language.implicitConversions

class JsArrayImplicitsSpec
{
  @Test
  def test_implicits_should_turn_objects_into_jsvalues(): Unit =
  {

    Assert.assertTrue(JsArray(1,
                              1.2,
                              true,
                              BigDecimal(1.5),
                              BigInt(10),
                              JsNull,
                              JsObj.empty,
                              JsArray.empty,
                              "a",
                              10L
                              ).size > 0
                      )


  }
}
