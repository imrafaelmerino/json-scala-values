package value.specs

import com.dslplatform.json.ParsingException
import org.scalatest.FlatSpec
import value.Implicits._
import value.spec.JsBoolSpecs.bool
import value.spec.JsNumberSpecs._
import value.spec.JsIntSpecs._
import value.spec.JsLongSpecs._
import value.spec.JsArraySpecs._
import value.spec.JsObjSpecs.obj
import value.spec.{*, JsObjSpec, JsObjSpecs, JsSpec}
import value.spec.JsStringSpecs.str
import value.{JsArray, JsNull, JsObj, JsObjParser}


class ObjParserSpec extends FlatSpec
{

  "parsing primitives types specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("a" -> "a",
                    "b" -> 1,
                    "c" -> true,
                    "d" -> 1.4,
                    "e" -> Long.MaxValue,
                    "f" -> BigDecimal(1.5),
                    "g" -> 100L,
                    "h" -> BigInt(1000)

                    )

    val spec = JsObjSpec("a" -> str,
                         "b" -> int,
                         "c" -> bool,
                         "d" -> decimal,
                         "e" -> number,
                         "f" -> decimal,
                         "g" -> long,
                         "h" -> integral
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of integers specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(1,
                                   2,
                                   3,
                                   4
                                   ),
                    "g" -> null,
                    "h" -> JsArray(1,
                                   2,
                                   3,
                                   JsNull
                                   ), //no vale null, ver si se puede converison
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> array_of_int,
                         "g" -> array_of_int_or_null,
                         "h" -> array_of_int_with_nulls,
                         "i" -> array_of_int_with_nulls_or_null
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of longs specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(1L,
                                   2L,
                                   3L,
                                   4L
                                   ),
                    "g" -> null,
                    "h" -> JsArray(1L,
                                   2L,
                                   3L,
                                   JsNull
                                   ), //no vale null, ver si se puede converison
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> array_of_long,
                         "g" -> array_of_long_or_null,
                         "h" -> array_of_long_with_nulls,
                         "i" -> array_of_long_with_nulls_or_null
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of bigdec specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(BigDecimal(1.2),
                                   BigDecimal(1.5),
                                   BigDecimal(1.12),
                                   BigDecimal(BigInt(100000000))
                                   ),
                    "g" -> null,
                    "h" -> JsArray(BigDecimal(1.2),
                                   BigDecimal(1.2),
                                   BigDecimal(1.2),
                                   JsNull
                                   ), //no vale null, ver si se puede converison
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> array_of_decimal,
                         "g" -> array_of_decimal_or_null,
                         "h" -> array_of_decimal_with_nulls,
                         "i" -> array_of_decimal_with_nulls_or_null
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of strings specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray("a",
                                   "b",
                                   "c",
                                   "d"
                                   ),
                    "g" -> null,
                    "h" -> JsArray("a",
                                   "hi",
                                   JsNull,
                                   "hi"
                                   )
                    ,
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> array_of_str,
                         "g" -> array_of_str_or_null,
                         "h" -> array_of_str_with_nulls,
                         "i" -> array_of_str_with_nulls_or_null
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of booleans specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(true,
                                   false,
                                   false,
                                   true
                                   ),
                    "g" -> null,
                    "h" -> JsArray(true,
                                   true,
                                   JsNull,
                                   false
                                   )
                    ,
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> array_of_bool,
                         "g" -> array_of_bool_or_null,
                         "h" -> array_of_bool_with_nulls,
                         "i" -> array_of_bool_with_nulls_or_null

                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of numbers specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(1L,
                                   2.5,
                                   BigInt("100000000000000000"),
                                   BigDecimal(10.5),
                                   3
                                   ),
                    "g" -> null,
                    "h" -> JsArray(1,
                                   Long.MaxValue,
                                   JsNull,
                                   10.5
                                   )
                    ,
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> array_of_number,
                         "g" -> array_of_number_or_null,
                         "h" -> array_of_number_with_nulls,
                         "i" -> array_of_number_with_nulls_or_null
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing arrays of integral numbers specifying a spec" should "parse the string into a json object" in
  {

    val obj = JsObj("f" -> JsArray(1L,
                                   2,
                                   BigInt("100000000000000000"),
                                   BigDecimal(10),
                                   3
                                   ),
                    "g" -> null,
                    "h" -> JsArray(10,
                                   Long.MaxValue,
                                   JsNull,
                                   10000L
                                   )
                    ,
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> array_of_integral,
                         "g" -> array_of_integral_or_null,
                         "h" -> array_of_integral_with_nulls,
                         "i" -> array_of_integral_with_nulls_or_null
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing nested objects specs of primitives types" should "parse the string into the same json object" in
  {

    val obj = JsObj("a" -> "a",
                    "b" -> 1,
                    "c" -> true,
                    "d" -> 1.4,
                    "e" -> Long.MaxValue,
                    "f" -> BigDecimal(1.5),
                    "g" -> 100L,
                    "h" -> BigInt(1000),
                    "i" -> JsObj("a" -> 1,
                                 "b" -> "hi",
                                 "c" -> JsObj("a" -> true,
                                              "d" -> "hi",
                                              "e" -> JsArray(1,
                                                             2,
                                                             3,
                                                             4
                                                             )
                                              ),

                                 ),
                    "j" -> JsArray(JsObj("a" -> 1,
                                         "b" -> "a"
                                         ),
                                   JsObj("a" -> 2,
                                         "b" -> "b"
                                         )
                                   )

                    )

    val spec = JsObjSpec("a" -> str,
                         "b" -> int,
                         "c" -> bool,
                         "d" -> decimal,
                         "e" -> number,
                         "f" -> decimal,
                         "g" -> long,
                         "h" -> integral,
                         "i" -> JsObjSpec("a" -> int,
                                          "b" -> str,
                                          "c" -> JsObjSpec("a" -> bool,
                                                           "d" -> str,
                                                           "e" -> array_of_int
                                                           )
                                          ),
                         "j" -> arrayOfObjSpec(JsObjSpec("a" -> int,
                                                         "b" -> str
                                                         )
                                               )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing array of values specs" should "parse the string into the same json object" in
  {

    val obj = JsObj("a" -> JsArray("a",
                                   1,
                                   true,
                                   2.5
                                   ),
                    "b" -> JsArray("a",
                                   1,
                                   true,
                                   2.5,
                                   JsNull
                                   ),
                    "c" -> null,
                    "d" -> JsArray(JsNull,
                                   1,
                                   2,
                                   "hi"
                                   )
                    )

    val spec = JsObjSpec("a" -> array,
                         "b" -> array_with_nulls,
                         "c" -> array_or_null,
                         "d" -> array_with_nulls_or_null
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())


  }

  "parsing array of string specs" should "parse the string into the same json object" in
  {

    val obj = JsObj("a" -> JsArray("a",
                                   "b",
                                   "c",
                                   "d"
                                   ),
                    "b" -> JsArray("a",
                                   "b",
                                   JsNull,
                                   "d",
                                   JsNull
                                   ),
                    "c" -> null,
                    "d" -> JsArray("a",
                                   "b",
                                   JsNull,
                                   "d",
                                   JsNull
                                   ),
                    )

    val spec = JsObjSpec("a" -> array_of_str,
                         "b" -> array_of_str_with_nulls,
                         "c" -> array_of_str_or_null,
                         "d" -> array_of_str_with_nulls_or_null
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def obj1 = parser.parse(obj.toString.getBytes)

    assert(obj == obj1 && obj.hashCode() == obj1.hashCode())

  }

  "parsing a key that doesn't match the int spec" should "fail if the element is not an integer" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> int
                                       )
                             )


    assertThrows[ParsingException](parser.parse(JsObj("a" -> true).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> BigDecimal.valueOf(1.5)).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> "hi").toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(parser.parse(JsObj("a" -> 10).toString.getBytes()) == JsObj("a" -> 10))

  }

  "parsing a key that doesn't match the long spec" should "fail if the element is not an long" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> long
                                       )
                             )

    assertThrows[ParsingException](parser.parse(JsObj("a" -> true).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> BigDecimal.valueOf(1.5)).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> "hi").toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(parser.parse(JsObj("a" -> 10).toString.getBytes()) == JsObj("a" -> 10))
    assert(parser.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()) == JsObj("a" -> Long.MaxValue))

  }

  "parsing a key that doesn't match the decimal spec" should "fail if the element is not a number" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> decimal
                                       )
                             )
    assertThrows[ParsingException](parser.parse(JsObj("a" -> true).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> "hi").toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(parser.parse(JsObj("a" -> 10).toString.getBytes()) == JsObj("a" -> 10))
    assert(parser.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()) == JsObj("a" -> Long.MaxValue))
    assert(parser.parse(JsObj("a" -> 1.5).toString.getBytes()) == JsObj("a" -> 1.5))
    assert(parser.parse(JsObj("a" -> BigInt("10000000000000")).toString.getBytes()) == JsObj("a" -> BigInt("10000000000000")))

  }

  "parsing a key that doesn't match the integral spec" should "fail if the element is not an integral number" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> integral
                                       )
                             )
    assertThrows[ParsingException](parser.parse(JsObj("a" -> true).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> "hi").toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(parser.parse(JsObj("a" -> 10).toString.getBytes()) == JsObj("a" -> 10))
    assert(parser.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()) == JsObj("a" -> Long.MaxValue))
    assert(parser.parse(JsObj("a" -> BigInt("10000000000000")).toString.getBytes()) == JsObj("a" -> BigInt("10000000000000")))

  }

  "parsing a key that doesn't match the string spec" should "fail if the element is not a string" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> str
                                       )
                             )
    assertThrows[ParsingException](parser.parse(JsObj("a" -> true).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 100).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> BigDecimal(1.5)).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(parser.parse(JsObj("a" -> "hi").toString.getBytes()) == JsObj("a" -> "hi"))

  }

  "parsing a key that doesn't match the bool spec" should "fail if the element is not a boolean" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> bool
                                       )
                             )
    assertThrows[ParsingException](parser.parse(JsObj("a" -> "true").toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> "false").toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 100).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> BigDecimal(1.5)).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(parser.parse(JsObj("a" -> true).toString.getBytes()) == JsObj("a" -> true))
    assert(parser.parse(JsObj("a" -> false).toString.getBytes()) == JsObj("a" -> false))

  }

  "parsing a key that doesn't match the object spec" should "fail if the element is not an object" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> obj
                                       )
                             )
    assertThrows[ParsingException](parser.parse(JsObj("a" -> "hi").toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> false).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 100).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> BigDecimal(1.5)).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(parser.parse(JsObj("a" -> JsObj.empty).toString.getBytes()) == JsObj("a" -> JsObj.empty))

  }

  "parsing a key that doesn't match the array spec" should "fail if the element is not an array" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> array
                                       )
                             )
    assertThrows[ParsingException](parser.parse(JsObj("a" -> "hi").toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> false).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> 100).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> BigDecimal(1.5)).toString.getBytes()))
    assertThrows[ParsingException](parser.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assert(parser.parse(JsObj("a" -> JsArray.empty).toString.getBytes()) == JsObj("a" -> JsArray.empty))

  }
}
