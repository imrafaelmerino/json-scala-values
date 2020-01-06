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
        JsArray.empty.asJsObj
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
