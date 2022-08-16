package json.value.spec.parser

import com.github.plokhotnyuk.jsoniter_scala.core.*
import json.value.*
import json.value.gen.JsObjGen
import json.value.spec.codec.*
import json.value.spec.parser.{JsValueParser, *}
import json.value.gen.Conversions.given
import json.value.spec.*
import org.scalacheck.Gen
import org.scalatest.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*


class JsParsersTests extends AnyFlatSpec with should.Matchers {

  "parsing an obj" should "return a JsObj" in {
    val codec = JsObjCodec(JsObjSpecParser(Map("a" -> JsIntParser.or(JsBoolParser), "b" -> JsStrParser.or(JsIntParser)), true, List.empty))

    val x = readFromString("{\n  \"a\": 1,\n  \"b\": \"hi\"\n}")(codec)

    x("a") should be(JsInt(1))
    x("b") should be(JsStr("hi"))

    val y = readFromString("{\n  \"a\": true,\n  \"b\": 10\n}")(codec)

    y("a") should be(JsBool.TRUE)
    y("b") should be(JsInt(10))

  }

  "parsing an array with a spec" should "return a JsArray" in {
    val codec = JsArrayCodec(JsArraySpecParser(List(JsIntParser, JsStrParser), true))

    val array = readFromString("[1,\"hi\"]")(codec)

    array(0) should be(JsInt(1))
    array(1) should be(JsStr("hi"))

  }

  "parsing an array" should "return a JsArray" in {
    val codec = JsObjCodec(JsObjSpecParser(
      Map("a" -> JsArrayOfParser(JsIntParser)), 
      true, 
      List.empty,
      JsValueParser.DEFAULT))

    val obj: JsObj = readFromString("{\"a\":[1, 2]}")(codec)

    val array: JsArray = obj.getArray("a").nn

    array.getInt(0) should be(1)
    array.getInt(1) should be(2)


  }


  "validate an valid json object with a spec" should "return no error" in {
    val spec = JsObjSpec("a" -> IsInt.or(IsBool), "b" -> IsStr)

    spec.validateAll(JsObj("a" -> JsInt(1), "b" -> JsStr("a"))) should be(LazyList.empty)

    spec.validateAll(JsObj("a" -> JsBool.TRUE, "b" -> JsStr("a"))) should be(LazyList.empty)


  }

  "validate an valid json array with a spec" should "return no error" in {
    val spec = IsTuple(IsInt(_ > 0), IsStr)

    val array = JsArray(JsInt(1), JsStr("a"))

    spec.validateAll(array) should be(LazyList.empty)

  }

/*  "error" should "a"  in {

    val a = readFromString("111")(new JsonValueCodec[Boolean]{
      override def decodeValue(in: JsonReader, default: Boolean): Boolean = in.readBoolean()

      override def encodeValue(x: Boolean, out: JsonWriter): Unit = out.writeVal(x)

      override def nullValue: Boolean = false
    })

    println(a)
  }*/
}

/*
111
unexpected end of input, offset: 0x00000003, buf:
  +----------+-------------------------------------------------+------------------+
    |          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f | 0123456789abcdef |
  +----------+-------------------------------------------------+------------------+
  | 00000000 | 31 31 31                                        | 111              |
  +----------+-------------------------------------------------+------------------+

1111
illegal boolean, offset: 0x00000000, buf:
  +----------+-------------------------------------------------+------------------+
    |          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f | 0123456789abcdef |
  +----------+-------------------------------------------------+------------------+
  | 00000000 | 31 31 31 31                                     | 1111             |
  +----------+-------------------------------------------------+------------------+*/
