package value.specs

import org.scalatest.{Assertions, FlatSpec}
import value.{FALSE, JsArray, JsBigDec, JsBigInt, JsBool, JsDouble, JsInt, JsLong, JsNothing, JsNull, JsObj, JsStr, TRUE, UserError}
import value.Preamble._
class JsValueSpec extends FlatSpec
{

  "isXXX functions" should "evaluate the predicate or return false" in {

    val jsInt = JsInt(1)
    val jsLong = JsLong(1)
    val jsStr = JsStr("hi")
    val jsObj = JsObj("a"->1)
    val jsArr = JsArray("a")
    val jsDouble = JsDouble(1.5)
    val jsDecimal = JsBigDec(1.5)
    val jsBigInt = JsBigInt(1)

    Assertions.assert(jsDouble.isDouble)
    Assertions.assert(jsDouble.isDouble(i=>i==1.5))

    Assertions.assert(jsDecimal.isDecimal)
    Assertions.assert(jsDecimal.isDecimal(i=> i == 1.5))

    Assertions.assert(jsBigInt.isIntegral)
    Assertions.assert(jsBigInt.isIntegral(i=> i == BigInt(1)))

    Assertions.assert(jsInt.isInt)
    Assertions.assert(jsInt.isInt(i=>i==1))

    Assertions.assert(jsLong.isLong)
    Assertions.assert(jsLong.isLong(i=>i==1))

    Assertions.assert(jsStr.isStr)
    Assertions.assert(jsStr.isStr(i=>i.startsWith("h")))

    Assertions.assert(jsStr.isNotJson)

    Assertions.assert(jsObj.isObj)
    Assertions.assert(jsObj.isObj(i=>i.size==1))

    Assertions.assert(jsObj.isJson)
    Assertions.assert(jsObj.isJson(i=>i.size==1))

    Assertions.assert(jsArr.isArr)
    Assertions.assert(jsArr.isArr(i=>i.size==1))

  }

  "TRUE and FALSE" should "be the only instances of JsBool" in{

    assert(JsBool(true) === TRUE)
    assert(JsBool(false) === FALSE)
  }

  "JsStr toXXX methods" should "throw an UserError" in {

    val str = JsStr("a")
    assertThrows[UserError]{
      str.toJsArray
    }
    assertThrows[UserError]{
      str.toJsBigDec
    }
    assertThrows[UserError]{
      str.toJsBigInt
    }
    assertThrows[UserError]{
      str.toJsBool
    }
    assertThrows[UserError]{
      str.toJsDouble
    }
    assertThrows[UserError]{
      str.toJsInt
    }
    assertThrows[UserError]{
      str.toJsLong
    }
    assertThrows[UserError]{
      str.toJsNull
    }
    assertThrows[UserError]{
      str.toJsNumber
    }
    assertThrows[UserError]{
      str.toJsObj
    }
  }

  "JsStr isXXX methods" should "return false" in {

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

  "JsLong toXXX methods" should "throw an UserError" in {

    val long = JsLong(1)
    assertThrows[UserError]{
      long.toJsArray
    }

    assertThrows[UserError]{
      long.toJsBool
    }

    assertThrows[UserError]{
      long.toJsInt
    }

    assertThrows[UserError]{
      long.toJsNull
    }

    assertThrows[UserError]{
      long.toJsObj
    }
  }

  "JsLong isXXX methods" should "return false" in {

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


  "JsInt toXXX methods" should "throw an UserError" in {

    val int = JsInt(1)
    assertThrows[UserError]{
      int.toJsArray
    }

    assertThrows[UserError]{
      int.toJsBool
    }

    assertThrows[UserError]{
      int.toJsNull
    }

    assertThrows[UserError]{
      int.toJsObj
    }
  }
  "JsInt isXXX methods" should "return false" in {

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
  "JsNull isXXX methods" should "return false" in {

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

  "JsNothing isXXX methods" should "return false" in {

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

  "JsNull toXXX methods" should "throw an UserError" in {

    assertThrows[UserError]{
      JsNull.toJsArray
    }
    assertThrows[UserError]{
      JsNull.toJsBigDec
    }
    assertThrows[UserError]{
      JsNull.toJsBigInt
    }
    assertThrows[UserError]{
      JsNull.toJsBool
    }
    assertThrows[UserError]{
      JsNull.toJsDouble
    }
    assertThrows[UserError]{
      JsNull.toJsInt
    }
    assertThrows[UserError]{
      JsNull.toJsLong
    }

    assertThrows[UserError]{
      JsNull.toJsNumber
    }
    assertThrows[UserError]{
      JsNull.toJsObj
    }
    assertThrows[UserError]{
      JsNull.toJsStr
    }

  }

  "JsNothing toXXX methods" should "throw an UserError" in {

    assertThrows[UserError]{
      JsNothing.toJsArray
    }
    assertThrows[UserError]{
      JsNothing.toJsBigDec
    }
    assertThrows[UserError]{
      JsNothing.toJsBigInt
    }
    assertThrows[UserError]{
      JsNothing.toJsBool
    }
    assertThrows[UserError]{
      JsNothing.toJsDouble
    }
    assertThrows[UserError]{
      JsNothing.toJsInt
    }
    assertThrows[UserError]{
      JsNothing.toJsLong
    }
    assertThrows[UserError]{
      JsNothing.toJsNull
    }
    assertThrows[UserError]{
      JsNothing.toJsNumber
    }
    assertThrows[UserError]{
      JsNothing.toJsObj
    }
    assertThrows[UserError]{
      JsNothing.toJsStr
    }

  }
}
