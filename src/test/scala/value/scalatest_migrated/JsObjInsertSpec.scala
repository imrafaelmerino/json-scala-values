package value.scalatest_migrated
import scala.language.implicitConversions
import org.junit.{Assert, Test}
import value.{JsInt, JsObj}
import value.Preamble.{given}


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
