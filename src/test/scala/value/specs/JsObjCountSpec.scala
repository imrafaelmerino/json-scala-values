package value.specs
import scala.language.implicitConversions

import org.junit.{Assert, Test}
import value.{JsObj, JsPath, JsValue}


class JsObjCountSpec
{
  @Test
  def test_count_pairs_in_an_empty_object_should_return_zero(): Unit =
  {
    val empty = JsObj()
    Assert.assertTrue(empty.count((_: (JsPath, JsValue)) => true) == 0)
  }
}
