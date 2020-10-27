package json.value

import json.value.{JsObj, JsPath, JsValue}
import org.junit.{Assert, Test}

import scala.language.implicitConversions


class JsObjCountSpec
{
  @Test
  def test_count_pairs_in_an_empty_object_should_return_zero(): Unit =
  {
    val empty = JsObj.empty
    Assert.assertTrue(empty.count((_: (JsPath, JsValue)) => true) == 0)
  }
}
