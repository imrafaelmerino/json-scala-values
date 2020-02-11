package value.scalatest_migrated
import scala.language.implicitConversions
import value.Preamble._
import org.junit.{Assert, Test}
import value.JsObj
import value.JsObj.empty

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
