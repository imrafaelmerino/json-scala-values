package value.specs

import org.scalatest.FlatSpec
import value.Preamble._
import value.{JsArray, JsInt, JsNothing, JsObj, JsPath, JsValue, UserError}


class JsObjSpec extends FlatSpec
{


  "filterKey" should "delete keys that dont satisfies the predicate" in
  {

    val a = JsObj("a" -> 1,
                  "b" -> 2,
                  "c" -> "hi",
                  "d" -> JsObj("a" -> 1,
                               "b" -> JsArray(JsObj("a" -> 1),
                                              true
                                              )
                               )
                  )

    val b = a.filterKey(k => k != "a")

    val filter: ((JsPath, JsValue)) => Boolean = (p: (JsPath,  JsValue)) => p._1.last.isKey(k => k == "a")
    val flatten: LazyList[(JsPath, JsValue)] = b.flatten
    assert(flatten.count(filter)==0)

  }

  "classificatory methods" should "return false but isObj and isJson" in {

    assert(!JsObj.empty.isArr)
    assert(!JsObj.empty.isNothing)
    assert(!JsObj.empty.isNumber)
    assert(!JsObj.empty.isStr)
    assert(!JsObj.empty.isInt)
    assert(!JsObj.empty.isLong)
    assert(!JsObj.empty.isDouble)
    assert(!JsObj.empty.isBigInt)
    assert(!JsObj.empty.isBigDec)
    assert(!JsObj.empty.isBool)
    assert(!JsObj.empty.isNull)
    assert(JsObj.empty.isObj)
    assert(JsObj.empty.isJson)
    assert(JsObj.empty.isJson(o=>o.isEmpty))
    assert(JsObj.empty.isJson(o=> !o.isNotEmpty))

  }

  "lastOption and headOption" should "return an option" in {
    assert(JsObj.empty.headOption == None)
    assert(JsObj.empty.lastOption == None)
    assert(JsObj("a"->1).headOption == Some(("a",JsInt(1))))
    assert(JsObj("a"->1).lastOption == Some(("a",JsInt(1))))
  }

  "keySet" should "return keys in a set" in {
   val a = JsObj("a"->1,"b"->2,"c"->JsArray(1,2,3))
    assert(a.keySet == Set("a","b","c"))
  }

  "asJsXX" should "throw an UserError" in {

    assertThrows[UserError]
      {
        JsObj.empty.asJsArray
      }

    assertThrows[UserError]
      {
        JsObj.empty.asJsInt
      }

    assertThrows[UserError]
      {
        JsObj.empty.asJsDouble
      }
    assertThrows[UserError]
      {
        JsObj.empty.asJsLong
      }
    assertThrows[UserError]
      {
        JsObj.empty.asJsNull
      }

    assertThrows[UserError]
      {
        JsObj.empty.asJsNumber
      }

    assertThrows[UserError]
      {
        JsObj.empty.asJsStr
      }

    assertThrows[UserError]
      {
        JsObj.empty.asJsBool
      }

    assertThrows[UserError]
      {
        JsObj.empty.asJsBigInt
      }

    assertThrows[UserError]
      {
        JsObj.empty.asJsBigDec
      }
  }

  "apply function" should "return JsNothing" in {
    assert(JsObj.empty(1) == JsNothing)
  }

  "iterator of empty" should "return an empty iterator" in {
    assert(JsObj.empty.iterator.isEmpty)
  }

  "iterator of one element" should "be exhausted after one next" in {
    val iterator = JsObj("a" -> 1).iterator
    assert(iterator.hasNext)
    iterator.next()
    assert(!iterator.hasNext)
  }

}
