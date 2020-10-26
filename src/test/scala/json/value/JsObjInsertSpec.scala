package json.value

import json.value.Preamble.{_, given _}
import json.value.{JsInt, JsObj}
import org.junit.{Assert, Test}

import scala.language.implicitConversions


class JsObjInsertSpec
{


  @Test
  def test_implicit_conversion_should_be_applied_when_inserting_values_in_Json_objects(): Unit =
  {

    val a = JsObj.empty
    val b = a.inserted("a",
                       1
                       )
    Assert.assertTrue(b("a") == JsInt(1))

  }
}
