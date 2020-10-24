package json.value.scalatest_migrated

import org.junit.{Assert, Test}
import json.value.{JsArrayParser, JsObjParser}
import scala.language.implicitConversions

class MalformedExceptionSpec
{

  @Test
  def test_parsing_a_malformed_Json_object_should_wraps_an_exception_in_a_Try_computation_in(): Unit =
  {

    Assert.assertTrue(JsObjParser.parse("{").isLeft)

    Assert.assertTrue(!JsObjParser.parse("{").isRight)

  }

  @Test
  def test_parsing_a_malformed_Json_array_should_wraps_an_exception_in_a_Try_computation_in(): Unit =
  {

    Assert.assertTrue(JsArrayParser.parse("[1,").isLeft)

    Assert.assertTrue(!JsArrayParser.parse("[1,").isRight)

  }

  @Test
  def test_parsing_an_object_with_JsArrayparse_str_should_throw_a_MalformedJson_exception_in(): Unit
  =
  {

    Assert.assertTrue(JsArrayParser.parse("{}").isLeft)

    Assert.assertTrue(!JsArrayParser.parse("{}").isRight)

  }

  @Test
  def test_parsing_an_object_with_JsArray_parse_bytes_should_throw_a_MalformedJson_exception_in(): Unit =
  {

    Assert.assertTrue(JsArrayParser.parse("{}".getBytes()).isLeft)

    Assert.assertTrue(!JsArrayParser.parse("{}".getBytes()).isRight)

  }

  @Test
  def test_parsing_an_array_with_JsObj_parse_str_should_throw_a_MalformedJson_exception_in(): Unit =
  {

    Assert.assertTrue(JsObjParser.parse("[]").isLeft)

    Assert.assertTrue(!JsObjParser.parse("[]").isRight)

  }

  @Test
  def test_parsing_an_array_with_JsObj_parsebytes_should_throw_a_MalformedJson_exception_in(): Unit =
  {
    Assert.assertTrue(JsObjParser.parse("[]".getBytes()).isLeft)

    Assert.assertTrue(!JsObjParser.parse("[]".getBytes()).isRight)

  }
}
