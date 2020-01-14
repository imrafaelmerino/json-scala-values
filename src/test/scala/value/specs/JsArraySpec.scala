package value.specs

import value.Preamble._
import org.scalatest.FlatSpec
import value.{JsArray, JsNothing, JsObj, UserError}

class JsArraySpec extends FlatSpec
{
  "asJsXX" should "throw an UserError" in
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

  "" should "" in
  {
    val a = JsArray(1,
                    2,
                    3
                    )
    val b = JsArray(4,
                    5,
                    6
                    )
    assert(a.appendedAll(b) == b.prependedAll(a
                                             )
           )


  }


}
