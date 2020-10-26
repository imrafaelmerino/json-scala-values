package json.value.scalatest_migrated

import scala.language.implicitConversions
import org.junit.{Assert, Test}
import json.value.spec.Preamble.{_, given _}
import json.value.Preamble.{_, given _}
import json.value._
import scala.quoted.{_, given _}
import scala.quoted.Expr


class JsObjSpec
{



  @Test
  def test_filterKey_should_delete_keys_that_dont_satisfies_the_predicate_in(): Unit =
  {

    val b = JsObj("a" -> 1,
                  "b" -> 2,
                  "c" -> "hi",
                  "d" -> JsObj("a" -> 1,
                               "b" -> JsArray(JsObj("a" -> 1),
                                              true
                                              )
                               )
                  )


    val filter: ((JsPath, JsValue)) => Boolean = (p: (JsPath, JsValue)) => p._1.last.isKey(k => k == "a")
    val flatten: LazyList[(JsPath, JsValue)] = b.flatten
    Assert.assertTrue(flatten.count(filter) == 3)

  }

  @Test
  def test_classificatory_methods_should_return_false_but_isObj_and_isJson_in_(): Unit =
  {
    Assert.assertTrue(!JsObj.empty.isArr)
    Assert.assertTrue(!JsObj.empty.isNothing)
    Assert.assertTrue(!JsObj.empty.isNumber)
    Assert.assertTrue(!JsObj.empty.isStr)
    Assert.assertTrue(!JsObj.empty.isInt)
    Assert.assertTrue(!JsObj.empty.isLong)
    Assert.assertTrue(!JsObj.empty.isDouble)
    Assert.assertTrue(!JsObj.empty.isBigInt)
    Assert.assertTrue(!JsObj.empty.isBigDec)
    Assert.assertTrue(!JsObj.empty.isBool)
    Assert.assertTrue(!JsObj.empty.isNull)
    Assert.assertTrue(JsObj.empty.isObj)
    Assert.assertTrue(JsObj.empty.isJson)
    Assert.assertTrue(JsObj.empty.isJson(o => o.isEmpty))
    Assert.assertTrue(JsObj.empty.isJson(o => !o.isNotEmpty))

  }

  @Test
  def test_lastOption_and_headOption_should_return_an_option_in(): Unit =
  {
    Assert.assertTrue(JsObj.empty.headOption.isEmpty)
    Assert.assertTrue(JsObj.empty.lastOption.isEmpty)
    Assert.assertTrue(JsObj("a" -> 1).headOption.contains(("a", JsInt(1))))
    Assert.assertTrue(JsObj("a" -> 1).lastOption.contains(("a", JsInt(1))))
  }

  @Test
  def test_keySet_should_return_keys_in_a_set_in_(): Unit =
  {
    val a = JsObj("a" -> 1,
                  "b" -> 2,
                  "c" -> JsArray(1,
                                 2,
                                 3
                                 )
                  )
    Assert.assertTrue(a.keySet == Set("a",
                                      "b",
                                      "c"
                                      )
                      )
  }

  @Test
  def test_toJsXX_should_throw_an_UserError_in(): Unit =
  {


    //   Assert.assertTrueThrows[UserError]
    //      {
    //        jsobj.empty.tojsarray
    //      }
    //
    //    assert.asserttruethrows[userError]
    //      {
    //        jsobj.empty.tojsint
    //      }
    //
    //    assert.asserttruethrows[userError]
    //      {
    //        jsobj.empty.tojsdouble
    //      }
    //    assert.asserttruethrows[userError]
    //      {
    //        jsobj.empty.tojslong
    //      }
    //    assert.asserttruethrows[userError]
    //      {
    //        jsobj.empty.tojsnull
    //      }
    //
    //    assert.asserttruethrows[userError]
    //      {
    //        jsobj.empty.tojsnumber
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsObj.empty.toJsStr
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsObj.empty.toJsBool
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsObj.empty.toJsBigInt
    //      }
    //
    //    Assert.assertTrueThrows[UserError]
    //      {
    //        JsObj.empty.toJsBigDec
    //      }
  }

  @Test
  def test_apply_function_should_return_JsNothing_in_(): Unit =
  {
    Assert.assertTrue(JsObj.empty(1) == JsNothing)
  }

  @Test
  def test_iterator_of_empty_should_return_an_empty_iterator_in_(): Unit =
  {
    Assert.assertTrue(JsObj.empty.iterator.isEmpty)
  }

  @Test
  def test__iterator_of_one_element_should_be_exhausted_after_one_next_in_(): Unit =
  {
    val iterator = JsObj("a" -> 1).iterator
    Assert.assertTrue(iterator.hasNext)
    iterator.next()
    Assert.assertTrue(!iterator.hasNext)
  }

}
