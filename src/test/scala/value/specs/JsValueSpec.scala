package value.specs

import org.scalatest.{Assertions, FlatSpec}
import value.{JsArray, JsBigDec, JsBigInt, JsDouble, JsInt, JsLong, JsObj, JsStr}
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

}
