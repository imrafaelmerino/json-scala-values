package value.specs

import org.scalatest.{Assertions, FlatSpec}
import value.{FALSE, JsArray, JsBigDec, JsBigInt, JsBool, JsDouble, JsInt, JsLong, JsNothing, JsNull, JsObj, JsStr, TRUE, UserError}
import value.Preamble._

class JsValueSpec extends FlatSpec
{

  "isXXX functions" should "evaluate the predicate or return false" in
  {

    val jsInt = JsInt(1)
    val jsLong = JsLong(1)
    val jsStr = JsStr("hi")
    val jsObj = JsObj("a" -> 1)
    val jsArr = JsArray("a")
    val jsDouble = JsDouble(1.5)
    val jsDecimal = JsBigDec(1.5)
    val jsBigInt = JsBigInt(1)

    Assertions.assert(jsDouble.isDouble)
    Assertions.assert(jsDouble.isDouble(i => i == 1.5))

    Assertions.assert(jsDecimal.isDecimal)
    Assertions.assert(jsDecimal.isDecimal(i => i == 1.5))

    Assertions.assert(jsBigInt.isIntegral)
    Assertions.assert(jsBigInt.isIntegral(i => i == BigInt(1)))

    Assertions.assert(jsInt.isInt)
    Assertions.assert(jsInt.isInt(i => i == 1))

    Assertions.assert(jsLong.isLong)
    Assertions.assert(jsLong.isLong(i => i == 1))

    Assertions.assert(jsStr.isStr)
    Assertions.assert(jsStr.isStr(i => i.startsWith("h")))

    Assertions.assert(jsStr.isNotJson)

    Assertions.assert(jsObj.isObj)
    Assertions.assert(jsObj.isObj(i => i.size == 1))

    Assertions.assert(jsObj.isJson)
    Assertions.assert(jsObj.isJson(i => i.size == 1))

    Assertions.assert(jsArr.isArr)
    Assertions.assert(jsArr.isArr(i => i.size == 1))

  }

  "TRUE and FALSE" should "be the only instances of JsBool" in
  {

    assert(JsBool(true) === TRUE)
    assert(JsBool(false) === FALSE)
  }

  "JsStr toXXX methods" should "throw an UserError" in
  {

    val str = JsStr("a")
    assertThrows[UserError]
      {
        str.toJsArray
      }
    assertThrows[UserError]
      {
        str.toJsBigDec
      }
    assertThrows[UserError]
      {
        str.toJsBigInt
      }
    assertThrows[UserError]
      {
        str.toJsBool
      }
    assertThrows[UserError]
      {
        str.toJsDouble
      }
    assertThrows[UserError]
      {
        str.toJsInt
      }
    assertThrows[UserError]
      {
        str.toJsLong
      }
    assertThrows[UserError]
      {
        str.toJsNull
      }
    assertThrows[UserError]
      {
        str.toJsNumber
      }
    assertThrows[UserError]
      {
        str.toJsObj
      }
  }

  "JsStr isXXX methods" should "return false" in
  {

    val str = JsStr("a")
    assert(!str.isArr)
    assert(!str.isBigDec)
    assert(!str.isDecimal)
    assert(!str.isBigInt)
    assert(!str.isIntegral)
    assert(!str.isBool)
    assert(!str.isDouble)
    assert(!str.isInt)
    assert(!str.isLong)
    assert(!str.isNull)
    assert(!str.isNumber)
    assert(!str.isObj)
  }


  "toJsLong should " should "tobey widening primitive conversion rules" in
  {
    assert(JsLong(1L) == JsInt(1).toJsLong)
  }
  "JsLong toXXX methods" should "throw an UserError" in
  {

    val long = JsLong(1)
    assertThrows[UserError]
      {
        long.toJsArray
      }

    assertThrows[UserError]
      {
        long.toJsBool
      }

    assertThrows[UserError]
      {
        long.toJsInt
      }

    assertThrows[UserError]
      {
        long.toJsNull
      }

    assertThrows[UserError]
      {
        long.toJsObj
      }
  }

  "JsLong isXXX methods" should "return false" in
  {

    val long = JsLong(1L)
    assert(!long.isArr)
    assert(!long.isBigDec)
    assert(!long.isDecimal)
    assert(!long.isBigInt)
    assert(!long.isBool)
    assert(!long.isDouble)
    assert(!long.isInt)
    assert(!long.isNull)
    assert(!long.isObj)
  }


  "JsInt toXXX methods" should "throw an UserError" in
  {

    val int = JsInt(1)
    assertThrows[UserError]
      {
        int.toJsArray
      }

    assertThrows[UserError]
      {
        int.toJsBool
      }

    assertThrows[UserError]
      {
        int.toJsNull
      }

    assertThrows[UserError]
      {
        int.toJsObj
      }
  }
  "JsInt isXXX methods" should "return false" in
  {

    val int = JsInt(1)
    assert(!int.isArr)
    assert(!int.isBigDec)
    assert(!int.isDecimal)
    assert(!int.isBigInt)
    assert(!int.isBool)
    assert(!int.isDouble)
    assert(!int.isLong)
    assert(!int.isNull)
    assert(!int.isObj)
  }
  "JsNull isXXX methods" should "return false" in
  {

    assert(!JsNull.isArr)
    assert(!JsNull.isBigDec)
    assert(!JsNull.isDecimal)
    assert(!JsNull.isBigInt)
    assert(!JsNull.isIntegral)
    assert(!JsNull.isBool)
    assert(!JsNull.isDouble)
    assert(!JsNull.isInt)
    assert(!JsNull.isLong)
    assert(!JsNull.isNumber)
    assert(!JsNull.isObj)
    assert(!JsNull.isStr)
  }

  "JsNothing isXXX methods" should "return false" in
  {

    assert(!JsNothing.isArr)
    assert(!JsNothing.isBigDec)
    assert(!JsNothing.isDecimal)
    assert(!JsNothing.isBigInt)
    assert(!JsNothing.isIntegral)
    assert(!JsNothing.isBool)
    assert(!JsNothing.isDouble)
    assert(!JsNothing.isInt)
    assert(!JsNothing.isLong)
    assert(!JsNothing.isNull)
    assert(!JsNothing.isNumber)
    assert(!JsNothing.isObj)
    assert(!JsNothing.isStr)
  }

  "JsNull toXXX methods" should "throw an UserError" in
  {
    assertThrows[UserError]
      {
        JsNull.toJson
      }
    assertThrows[UserError]
      {
        JsNull.toJsArray
      }
    assertThrows[UserError]
      {
        JsNull.toJsBigDec
      }
    assertThrows[UserError]
      {
        JsNull.toJsBigInt
      }
    assertThrows[UserError]
      {
        JsNull.toJsBool
      }
    assertThrows[UserError]
      {
        JsNull.toJsDouble
      }
    assertThrows[UserError]
      {
        JsNull.toJsInt
      }
    assertThrows[UserError]
      {
        JsNull.toJsLong
      }

    assertThrows[UserError]
      {
        JsNull.toJsNumber
      }
    assertThrows[UserError]
      {
        JsNull.toJsObj
      }
    assertThrows[UserError]
      {
        JsNull.toJsStr
      }

  }

  "JsDouble toXXX methods" should "throw an UserError" in
  {
val d = JsDouble(1.2)
    assertThrows[UserError]
      {
        d.toJsArray
      }

    assertThrows[UserError]
      {
        d.toJson
      }

    assertThrows[UserError]
      {
        d.toJsBigInt
      }
    assertThrows[UserError]
      {
        d.toJsBool
      }
    assertThrows[UserError]
      {
        d.toJsInt
      }
    assertThrows[UserError]
      {
        d.toJsLong
      }
    assertThrows[UserError]
      {
        d.toJsObj
      }
    assertThrows[UserError]
      {
        d.toJsStr
      }

  }

  "JsNothing toXXX methods" should "throw an UserError" in
  {

    assertThrows[UserError]
      {
        JsNothing.toJsArray
      }
    assertThrows[UserError]
      {
        JsNothing.toJsBigDec
      }
    assertThrows[UserError]
      {
        JsNothing.toJsBigInt
      }
    assertThrows[UserError]
      {
        JsNothing.toJsBool
      }
    assertThrows[UserError]
      {
        JsNothing.toJsDouble
      }
    assertThrows[UserError]
      {
        JsNothing.toJsInt
      }
    assertThrows[UserError]
      {
        JsNothing.toJsLong
      }
    assertThrows[UserError]
      {
        JsNothing.toJsNull
      }
    assertThrows[UserError]
      {
        JsNothing.toJsNumber
      }
    assertThrows[UserError]
      {
        JsNothing.toJsObj
      }
    assertThrows[UserError]
      {
        JsNothing.toJsStr
      }

  }


  "JsBool toXXX methods" should "throw an UserError" in
  {

    val bool = JsBool(true)
    assertThrows[UserError]
      {
        bool.toJsArray
      }
    assertThrows[UserError]
      {
        bool.toJsBigDec
      }
    assertThrows[UserError]
      {
        bool.toJsBigInt
      }
    assertThrows[UserError]
      {
        bool.toJsDouble
      }
    assertThrows[UserError]
      {
        bool.toJsInt
      }
    assertThrows[UserError]
      {
        bool.toJsLong
      }

    assertThrows[UserError]
      {
        bool.toJsNumber
      }
    assertThrows[UserError]
      {
        bool.toJsObj
      }
    assertThrows[UserError]
      {
        bool.toJsStr
      }

  }

  "JsBool isXXX methods" should "return false" in
  {
    val bool = JsBool(true)

    assert(!bool.isArr)
    assert(!bool.isBigDec)
    assert(!bool.isDecimal)
    assert(!bool.isBigInt)
    assert(!bool.isIntegral)
    assert(!bool.isDouble)
    assert(!bool.isInt)
    assert(!bool.isLong)
    assert(!bool.isNull)
    assert(!bool.isNumber)
    assert(!bool.isObj)
    assert(!bool.isStr)
  }

  "toJsDouble should " should "tobey widening primitive conversion rules" in
  {
    assert(JsDouble(1L) == JsInt(1).toJsDouble)
    assert(JsDouble(1L) == JsLong(1L).toJsDouble)
    assert(JsDouble(1L) == JsDouble(1L).toJsDouble)
  }

  "toJsBigInt should " should "tobey widening primitive conversion rules" in
  {
    assert(JsBigInt(1L) == JsInt(1).toJsBigInt)
    assert(JsBigInt(1L) == JsLong(1L).toJsBigInt)
    assert(JsBigInt(1L) == JsBigInt(1L).toJsBigInt)
  }

  "toJsBigDec should " should "tobey widening primitive conversion rules" in
  {
    assert(JsBigDec(1) == JsInt(1).toJsBigDec)
    assert(JsBigDec(1L) == JsLong(1L).toJsBigDec)
    assert(JsBigDec(1L) == JsBigInt(1L).toJsBigDec)
    assert(JsBigDec(1L) == JsDouble(1L).toJsBigDec)
    assert(JsBigDec(1L) == JsBigDec(1L).toJsBigDec)
  }


  "JsDouble isXXX methods" should "return false" in
  {
    val double = JsDouble(1.2)

    assert(!double.isArr)
    assert(!double.isBigDec)
    assert(!double.isBigInt)
    assert(!double.isIntegral)
    assert(!double.isBool)
    assert(!double.isInt)
    assert(!double.isLong)
    assert(!double.isNull)
    assert(!double.isObj)
    assert(!double.isStr)
  }

  "JsBigDec isXXX methods" should "return false" in
  {
    val bd = JsBigDec(1.2)

    assert(!bd.isArr)
    assert(!bd.isDouble)
    assert(!bd.isBigInt)
    assert(!bd.isIntegral)
    assert(!bd.isBool)
    assert(!bd.isInt)
    assert(!bd.isLong)
    assert(!bd.isNull)
    assert(!bd.isObj)
    assert(!bd.isStr)
  }

  "JsBigInt isXXX methods" should "return false" in
  {
    val bi = JsBigInt(Long.MaxValue)

    assert(!bi.isArr)
    assert(!bi.isDouble)
    assert(!bi.isBigDec)
    assert(!bi.isBool)
    assert(!bi.isInt)
    assert(!bi.isLong)
    assert(!bi.isNull)
    assert(!bi.isObj)
    assert(!bi.isStr)
  }

  "JsBigDec toXXX methods" should "throw an UserError" in
  {
    val bd = BigDecimal(1.5)
    assertThrows[UserError]
      {
        bd.toJsArray
      }

    assertThrows[UserError]
      {
        bd.toJsBigInt
      }
    assertThrows[UserError]
      {
        bd.toJsBool
      }
    assertThrows[UserError]
      {
        bd.toJsDouble
      }
    assertThrows[UserError]
      {
        bd.toJsInt
      }
    assertThrows[UserError]
      {
        bd.toJsLong
      }

    assertThrows[UserError]
      {
        bd.toJsObj
      }
    assertThrows[UserError]
      {
        bd.toJsStr
      }
    assertThrows[UserError]
      {
        bd.toJsNull
      }

  }

  "JsBigInt toXXX methods" should "throw an UserError" in
  {
    val bi = JsBigDec(1)
    assertThrows[UserError]
      {
        bi.toJsArray
      }

    assertThrows[UserError]
      {
        bi.toJsBool
      }
    assertThrows[UserError]
      {
        bi.toJsDouble
      }
    assertThrows[UserError]
      {
        bi.toJsInt
      }
    assertThrows[UserError]
      {
        bi.toJsLong
      }

    assertThrows[UserError]
      {
        bi.toJsObj
      }
    assertThrows[UserError]
      {
        bi.toJsStr
      }
    assertThrows[UserError]
      {
        bi.toJsNull
      }

  }
}
