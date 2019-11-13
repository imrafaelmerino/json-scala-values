package value.specs

import org.scalatest.FlatSpec
import value.{JsArray, JsObj, MalformedJson}

class MalformedExceptionSpec extends FlatSpec
{

  "parsing a malformed Json object" should "wraps an exception in a Try computation" in
  {

    assert(JsObj.parse("{").isFailure)

    assert(!JsObj.parse("{").isSuccess)

    assertThrows[MalformedJson]
      {
        JsObj.parse("{").get
      }
  }

  "parsing a malformed Json array" should "wraps an exception in a Try computation" in
  {

    assert(JsArray.parse("[1,").isFailure)

    assert(!JsArray.parse("[1,").isSuccess)

    assertThrows[MalformedJson]
      {
        JsArray.parse("[1,").get
      }
  }
}
