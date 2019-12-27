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

  "parsing an object with JsArray.parse(str)" should "throw a MalformedJson exception" in
  {

    assert(JsArray.parse("{}").isFailure)

    assert(!JsArray.parse("{}").isSuccess)

    assertThrows[MalformedJson]
      {
        JsArray.parse("{}").get

      }
  }

  "parsing an object with JsArray.parse(bytes)" should "throw a MalformedJson exception" in
  {

    assert(JsArray.parse("{}".getBytes()).isFailure)

    assert(!JsArray.parse("{}".getBytes()).isSuccess)

    assertThrows[MalformedJson]
      {
        JsArray.parse("{}".getBytes()).get

      }
  }


  "parsing an array with JsObj.parse(str)" should "throw a MalformedJson exception" in
  {

    assert(JsObj.parse("[]").isFailure)

    assert(!JsObj.parse("[]").isSuccess)

    assertThrows[MalformedJson]
      {
        JsObj.parse("[]").get


      }
  }

  "parsing an array with JsObj.parse(bytes)" should "throw a MalformedJson exception" in
  {

    assert(JsObj.parse("[]".getBytes()).isFailure)

    assert(!JsObj.parse("[]".getBytes()).isSuccess)

    assertThrows[MalformedJson]
      {
        JsObj.parse("[]".getBytes()).get
      }
  }
}
