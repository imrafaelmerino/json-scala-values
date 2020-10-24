package json.value

import org.junit.{Assert, Test}
import json.value.JsArray.empty
import json.value.Preamble.{given}

import scala.language.implicitConversions

class ConcatArraySpec
{
  val a: JsArray = JsArray(1,
                           2,
                           true,
                           JsNull
                           )

  @Test
  def test_concat_empty_arrays_return_empty(): Unit =
  {
    Assert.assertTrue((empty concat empty) == empty)
  }

  @Test
  def test_concat_empty_array_with_other_returns_other(): Unit =
  {
    Assert.assertTrue((empty concat a) == a)
    Assert.assertTrue((a concat empty) == a)
  }

  @Test
  def test_concat_array_with_itself_returns_the_array(): Unit =
  {
    Assert.assertTrue((a concat a) == a)
  }

  @Test
  def test_concat_array_with_itself_as_multiset():Unit = {
    Assert.assertTrue(a.concat(a,JsArray.TYPE.MULTISET)==a.appendedAll(a))
  }

  @Test
  def test_concat_array_with_itself_as_set():Unit = {
    Assert.assertTrue(a.concat(a,JsArray.TYPE.SET)==a)
  }

  @Test
  def test_concat_array_with_itself_as_list():Unit = {
    Assert.assertTrue(a.concat(a,JsArray.TYPE.LIST)==a)
  }
}
