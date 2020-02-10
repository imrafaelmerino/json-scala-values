package value.specs
import scala.language.implicitConversions

import org.junit.{Assert, Test}
import value.Preamble._
import value.{JsInt, JsObj}


class JsObjInsertSpec
{


  @Test
  def test_implicit_conversion_should_be_applied_when_inserting_values_in_Json_objects(): Unit =
  {

    val a = JsObj()
    val b = a.inserted("a",
                       1
                       )
    Assert.assertTrue(b("a") == JsInt(1))

  }
}
