package json.value.scalatest_migrated

import scala.language.implicitConversions

import org.junit.{Assert, Test}
import json.value.Preamble.{given _}
import json.value._

class JsArraySpec
{
  @Test
  def test_toJsXX_should_throw_an_UserError(): Unit =
  {

    //    assertThrows[UserError]
    //      {
    //        JsArray.empty.toJsObj
    //      }
    //
    //    assertThrows[UserError]
    //      {
    //        JsObj.empty.toJsInt
    //      }
    //
    //    assertThrows[UserError]
    //      {
    //        JsObj.empty.toJsDouble
    //      }
    //    assertThrows[UserError]
    //      {
    //        JsObj.empty.toJsLong
    //      }
    //    assertThrows[UserError]
    //      {
    //        JsObj.empty.toJsNull
    //      }
    //
    //    assertThrows[UserError]
    //      {
    //        JsObj.empty.toJsNumber
    //      }
    //
    //    assertThrows[UserError]
    //      {
    //        JsObj.empty.toJsStr
    //      }
    //
    //    assertThrows[UserError]
    //      {
    //        JsObj.empty.toJsBool
    //      }
    //
    //    assertThrows[UserError]
    //      {
    //        JsObj.empty.toJsBigInt
    //      }
    //
    //    assertThrows[UserError]
    //      {
    //        JsObj.empty.toJsBigDec
    //      }

  }

  @Test
  def test_apply_function_should_return_JsNothing(): Unit =
  {
    Assert.assertTrue(JsArray.empty("a") == JsNothing)
  }

  @Test
  def test_appended_and_prepended_functions_should_return_the_same_array_switching_the_arguments(): Unit =
  {
    val a = JsArray(1,
                    2,
                    3
                    )
    val b = JsArray(4,
                    5,
                    6
                    )
    Assert.assertTrue(a.appendedAll(b) == b.prependedAll(a))

  }

  @Test
  def test_inserted_in_array_should_pad_with_0(): Unit =
  {
    val a = JsArray.empty.inserted(5,
                                   1,
                                   0
                                   )
    Assert.assertTrue(a.length() == 6)
    Assert.assertTrue(a(5) == JsInt(1))
    Assert.assertTrue(a(-1) == JsInt(1))
    Assert.assertTrue(a(0) == JsInt(0))
    Assert.assertTrue(a(1) == JsInt(0))
    Assert.assertTrue(a(2) == JsInt(0))
    Assert.assertTrue(a(3) == JsInt(0))
    Assert.assertTrue(a(4) == JsInt(0))
    Assert.assertTrue(a(6) == JsNothing)
  }

  @Test
  def test_head_should_return_first_element(): Unit =
  {

    val a = JsArray(1,
                    2,
                    3,
                    4,
                    5
                    )
    Assert.assertTrue(a.head == a(0))
  }

  @Test
  def test_last_should_return_the_last_element(): Unit =
  {
    val a = JsArray(1,
                    2,
                    3,
                    4,
                    5
                    )
    Assert.assertTrue(a.last == a(-1))
  }

  @Test
  def test_init_should_return_all_elements_but_last(): Unit =
  {
    val a = JsArray(1,
                    2,
                    3,
                    4,
                    5
                    )
    Assert.assertTrue(a.init == a.removed(-1))

  }

  @Test
  def test_tail_should_return_all_elements_but_first(): Unit =
  {
    val a = JsArray(1,
                    2,
                    3,
                    4,
                    5
                    )
    Assert.assertTrue(a.tail == a.removed(0))
  }

  @Test
  def test_flatmap_should_convert_every_element_into_an_array_and_flatten_the_result(): Unit =
  {
    val a = JsArray(1,
                    2,
                    3,
                    4,
                    5
                    )

    val b = a.flatMap(it => JsArray(it,
                                    it
                                    )
                      )

    Assert.assertTrue(b == JsArray(1,
                                   1,
                                   2,
                                   2,
                                   3,
                                   3,
                                   4,
                                   4,
                                   5,
                                   5
                                   )
                      )
  }




}
