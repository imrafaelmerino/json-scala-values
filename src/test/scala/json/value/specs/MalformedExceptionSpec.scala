package json.value.specs

import org.scalatest.FlatSpec
import json.value.{ JsArrayParser, JsObjParser}

class MalformedExceptionSpec extends FlatSpec
{

  "parsing a malformed Json object" should "wraps an exception in a Try computation" in
  {

    assert(JsObjParser.parse("{").isLeft)

    assert(!JsObjParser.parse("{").isRight)

  }

  "parsing a malformed Json array" should "wraps an exception in a Try computation" in
  {

    assert(JsArrayParser.parse("[1,").isLeft)

    assert(!JsArrayParser.parse("[1,").isRight)

  }

  "parsing an object with JsArray.parse(str)" should "throw a MalformedJson exception" in
  {

    assert(JsArrayParser.parse("{}").isLeft)

    assert(!JsArrayParser.parse("{}").isRight)

  }

  "parsing an object with JsArray.parse(bytes)" should "throw a MalformedJson exception" in
  {

    assert(JsArrayParser.parse("{}".getBytes()).isLeft)

    assert(!JsArrayParser.parse("{}".getBytes()).isRight)

  }

  "parsing an array with JsObj.parse(str)" should "throw a MalformedJson exception" in
  {

    assert(JsObjParser.parse("[]").isLeft)

    assert(!JsObjParser.parse("[]").isRight)

  }

  "parsing an array with JsObj.parse(bytes)" should "throw a MalformedJson exception" in
  {
    assert(JsObjParser.parse("[]".getBytes()).isLeft)

    assert(!JsObjParser.parse("[]".getBytes()).isRight)

  }
}
