package json.value.scalatest_migrated

import org.junit.{Assert, Test}
import json.value.Preamble.{given}
import json.value.JsPath
import json.value.{JsArray, JsInt, JsNothing, JsObj}
import scala.language.implicitConversions
class MinusOneIndexSpec
{


  @Test
  def test_getting_value_located_at_index_should_return_the_last_element_in(): Unit =
  {


    val o:JsObj = JsObj("a" -> JsArray(1,
                                 2,
                                 3
                                 ),
                  "b" -> JsArray.empty
                  )



    Assert.assertTrue(o("a" / -1) == JsInt(3))
    Assert.assertTrue(o("a" / -1) == JsInt(3))
    Assert.assertTrue(o("a" / -1) == JsInt(3))
    Assert.assertTrue(o("b" / -1) == JsNothing)
    Assert.assertTrue(o("b" / -1) == JsNothing)
    Assert.assertTrue(o("b" / 0) == JsNothing)
    Assert.assertTrue(o("b" / -1) == JsNothing)
  }

  @Test
  def test_insert_value_at_index_should_replace_the_last_element_or_append_if_the_array_is_empty_in(): Unit =
  {

    val o = JsObj("a" -> JsArray(1,
                                 2,
                                 3
                                 ),
                  "b" -> JsArray.empty
                  )


    val a = o.inserted("a" / -1,
                       5
                       ).inserted("b" / -1,
                                  0
                                  )
    Assert.assertTrue(a("a" / -1) == JsInt(5)
                      )
    Assert.assertTrue(a("a" / 2) == JsInt(5)
                      )
    Assert.assertTrue(a("a" / 0) == JsInt(1)
                      )
    Assert.assertTrue(a("a" / 1) == JsInt(2)
                      )
    Assert.assertTrue(a("b" / 0) == JsInt(0)
                      )
    Assert.assertTrue(a("b" / -1) == JsInt(0)
                      )
  }

}
