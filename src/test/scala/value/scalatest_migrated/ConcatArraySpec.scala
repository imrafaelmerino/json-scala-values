package value.scalatest_migrated

import org.junit.{Assert, Test}
import value.JsArray
import value.JsArray.empty
import value.Preamble._

import scala.language.implicitConversions

class ConcatArraySpec
{
  val a = JsArray(1,
                  2,
                  true
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
}
