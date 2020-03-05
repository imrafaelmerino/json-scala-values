package value

import org.junit.{Assert, Test}
import value.JsObj.empty
import value.Preamble.{given}

import scala.language.implicitConversions

class ConcatObjSpec
{
  val a = JsObj("a" -> 1,
                "b" -> 2,
                "c" -> true
                )

  @Test
  def test_concat_empty_objects_return_empty(): Unit =
  {
    Assert.assertTrue((empty concat empty) == empty)
  }

  @Test
  def test_concat_empty_object_with_other_returns_other(): Unit =
  {
    Assert.assertTrue((empty concat a) == a)
    Assert.assertTrue((a concat empty) == a)
  }

  @Test
  def test_concat_object_with_itself_returns_the_object(): Unit =
  {
    Assert.assertTrue((a concat a) == a)
  }
}
