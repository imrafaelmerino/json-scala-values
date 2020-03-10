package value.specs

import value.Preamble._
import org.scalatest.FlatSpec
import value.{JsArray, JsInt, JsNothing, JsObj, UserError}

class JsArraySpecSpec extends FlatSpec
{
  "toJsXX" should "throw an UserError" in
  {

    assertThrows[UserError]
      {
        JsArray.empty.toJsObj
      }

    assertThrows[UserError]
      {
        JsObj.empty.toJsInt
      }

    assertThrows[UserError]
      {
        JsObj.empty.toJsDouble
      }
    assertThrows[UserError]
      {
        JsObj.empty.toJsLong
      }
    assertThrows[UserError]
      {
        JsObj.empty.toJsNull
      }

    assertThrows[UserError]
      {
        JsObj.empty.toJsNumber
      }

    assertThrows[UserError]
      {
        JsObj.empty.toJsStr
      }

    assertThrows[UserError]
      {
        JsObj.empty.toJsBool
      }

    assertThrows[UserError]
      {
        JsObj.empty.toJsBigInt
      }

    assertThrows[UserError]
      {
        JsObj.empty.toJsBigDec
      }

  }
  "apply function" should "return JsNothing" in
  {
    assert(JsArray.empty("a") == JsNothing)
  }
  "appended and prepended functions" should "return the same array switching the arguments" in
  {
    val a = JsArray(1,
                    2,
                    3
                    )
    val b = JsArray(4,
                    5,
                    6
                    )
    assert(a.appendAll(b) == b.prependedAll(a
                                              )
           )


  }
  "inserted in array" should "pad with 0" in
  {
    val a = JsArray.empty.inserted(5,
                                   1,
                                   0
                                   )
    assert(a.length() == 6)
    assert(a(5) == JsInt(1))
    assert(a(-1) == JsInt(1))
    assert(a(0) == JsInt(0))
    assert(a(1) == JsInt(0))
    assert(a(2) == JsInt(0))
    assert(a(3) == JsInt(0))
    assert(a(4) == JsInt(0))
    assert(a(6) == JsNothing)
  }
  "head" should "return first element" in {
    val a = JsArray(1,2,3,4,5)
    assert(a.head == a(0))
  }

  "last" should "return the last element" in {
    val a = JsArray(1,2,3,4,5)
    assert(a.last == a(-1))
  }

  "init" should "return all elements but last" in {
    val a = JsArray(1,2,3,4,5)
    assert(a.init == a.remove(-1))

  }

  "tail" should "return all elements but first" in {
    val a = JsArray(1,2,3,4,5)
    assert(a.tail == a.remove(0))
  }

  "flatmap" should "convert every element into an array and flatten the result" in {
    val a = JsArray(1,2,3,4,5)

    val b = a.flatMap(it=>JsArray(it,it))

    assert(b == JsArray(1,1,2,2,3,3,4,4,5,5))
  }


}


