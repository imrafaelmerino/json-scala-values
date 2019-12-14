package value.specs

import com.dslplatform.json.ParsingException
import org.scalatest.FlatSpec
import value.Implicits._
import value.spec.JsArraySpecs._
import value.spec.JsBoolSpecs.{bool, bool_or_null}
import value.spec.JsIntSpecs._
import value.spec.JsLongSpecs._
import value.spec.JsNumberSpecs._
import value.spec.JsObjSpecs.obj
import value.spec.JsStrSpecs.{str, str_or_null}
import value.spec.{JsNumberSpecs, JsObjSpec}
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

    val spec = JsObjSpec("a" -> array_of_value,
                         "b" -> array_of_value_with_nulls,
                         "c" -> array_of_value_or_null,
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

    val a_int = JsObjParser(JsObjSpec("a" -> int
                                      )
                            )

    val a_int_or_null = JsObjParser(JsObjSpec("a" -> int_or_null
                                              )
                                    )


    assertThrows[ParsingException](a_int.parse(JsObj("a" -> true).toString.getBytes()))
    assertThrows[ParsingException](a_int.parse(JsObj("a" -> "123").toString.getBytes()))
    assertThrows[ParsingException](a_int.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](a_int.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()))
    assertThrows[ParsingException](a_int.parse(JsObj("a" -> BigDecimal.valueOf(1.5)).toString.getBytes()))
    assertThrows[ParsingException](a_int.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](a_int.parse(JsObj("a" -> "hi").toString.getBytes()))
    assertThrows[ParsingException](a_int.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](a_int.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(a_int.parse(JsObj("a" -> 10).toString.getBytes()) == JsObj("a" -> 10))
    assert(a_int_or_null.parse(JsObj("a" -> JsNull).toString.getBytes()) == JsObj("a" -> JsNull))

  }

  "parsing a key that doesn't match the long spec" should "fail if the element is not an long" in
  {

    val a_long = JsObjParser(JsObjSpec("a" -> long
                                       )
                             )

    val a_long_or_null = JsObjParser(JsObjSpec("a" -> long_or_null
                                               )
                                     )

    assertThrows[ParsingException](a_long.parse(JsObj("a" -> true).toString.getBytes()))
    assertThrows[ParsingException](a_long.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](a_long.parse(JsObj("a" -> "10000").toString.getBytes()))
    assertThrows[ParsingException](a_long.parse(JsObj("a" -> BigDecimal.valueOf(1.5)).toString.getBytes()))
    assertThrows[ParsingException](a_long.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](a_long.parse(JsObj("a" -> "hi").toString.getBytes()))
    assertThrows[ParsingException](a_long.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](a_long.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(a_long.parse(JsObj("a" -> 10).toString.getBytes()) == JsObj("a" -> 10))
    assert(a_long.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()) == JsObj("a" -> Long.MaxValue))
    assert(a_long_or_null.parse(JsObj("a" -> JsNull).toString.getBytes()) == JsObj("a" -> JsNull))

  }

  "parsing a key that doesn't match the decimal spec" should "fail if the element is not a number" in
  {

    val a_decimal = JsObjParser(JsObjSpec("a" -> decimal
                                          )
                                )

    val a_decimal_or_null = JsObjParser(JsObjSpec("a" -> decimal_or_null
                                                  )
                                        )
    assertThrows[ParsingException](a_decimal.parse(JsObj("a" -> true).toString.getBytes()))
    assertThrows[ParsingException](a_decimal.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](a_decimal.parse(JsObj("a" -> "1.50").toString.getBytes()))
    assertThrows[ParsingException](a_decimal.parse(JsObj("a" -> "hi").toString.getBytes()))
    assertThrows[ParsingException](a_decimal.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](a_decimal.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(a_decimal.parse(JsObj("a" -> 10).toString.getBytes()) == JsObj("a" -> 10))
    assert(a_decimal.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()) == JsObj("a" -> Long.MaxValue))
    assert(a_decimal.parse(JsObj("a" -> 1.5).toString.getBytes()) == JsObj("a" -> 1.5))
    assert(a_decimal.parse(JsObj("a" -> BigInt("10000000000000")).toString.getBytes()) == JsObj("a" -> BigInt("10000000000000")))
    assert(a_decimal_or_null.parse(JsObj("a" -> JsNull).toString.getBytes()) == JsObj("a" -> JsNull))

  }

  "parsing a key that doesn't match the integral spec" should "fail if the element is not an integral number" in
  {

    val a_integral = JsObjParser(JsObjSpec("a" -> integral
                                           )
                                 )

    val a_integral_or_null = JsObjParser(JsObjSpec("a" -> JsNumberSpecs.integral_or_null
                                                   )
                                         )
    assertThrows[ParsingException](a_integral.parse(JsObj("a" -> true).toString.getBytes()))
    assertThrows[ParsingException](a_integral.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](a_integral.parse(JsObj("a" -> "10000").toString.getBytes()))
    assertThrows[ParsingException](a_integral.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](a_integral.parse(JsObj("a" -> "hi").toString.getBytes()))
    assertThrows[ParsingException](a_integral.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](a_integral.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(a_integral.parse(JsObj("a" -> 10).toString.getBytes()) == JsObj("a" -> 10))
    assert(a_integral.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()) == JsObj("a" -> Long.MaxValue))
    assert(a_integral.parse(JsObj("a" -> BigInt("10000000000000")).toString.getBytes()) == JsObj("a" -> BigInt("10000000000000")))
    assert(a_integral_or_null.parse(JsObj("a" -> JsNull).toString.getBytes()) == JsObj("a" -> JsNull))

  }

  "parsing a key that doesn't match the string spec" should "fail if the element is not a string" in
  {

    val a_string = JsObjParser(JsObjSpec("a" -> str
                                         )
                               )

    val a_null_or_string = JsObjParser(JsObjSpec("a" -> str_or_null
                                                 )
                                       )
    assertThrows[ParsingException](a_string.parse(JsObj("a" -> true).toString.getBytes()))
    assertThrows[ParsingException](a_string.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](a_string.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](a_string.parse(JsObj("a" -> 100).toString.getBytes()))
    assertThrows[ParsingException](a_string.parse(JsObj("a" -> BigDecimal(1.5)).toString.getBytes()))
    assertThrows[ParsingException](a_string.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](a_string.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(a_string.parse(JsObj("a" -> "hi").toString.getBytes()) == JsObj("a" -> "hi"))
    assert(a_null_or_string.parse(JsObj("a" -> JsNull).toString.getBytes()) == JsObj("a" -> JsNull))

  }

  "parsing a key that doesn't match the bool spec" should "fail if the element is not a boolean" in
  {

    val a_boolean = JsObjParser(JsObjSpec("a" -> bool
                                          )
                                )

    val a_null_or_boolean = JsObjParser(JsObjSpec("a" -> bool_or_null
                                                  )
                                        )
    assertThrows[ParsingException](a_boolean.parse(JsObj("a" -> "true").toString.getBytes()))
    assertThrows[ParsingException](a_boolean.parse(JsObj("a" -> "false").toString.getBytes()))
    assertThrows[ParsingException](a_boolean.parse(JsObj("a" -> JsNull).toString.getBytes()))
    assertThrows[ParsingException](a_boolean.parse(JsObj("a" -> 1.5).toString.getBytes()))
    assertThrows[ParsingException](a_boolean.parse(JsObj("a" -> 100).toString.getBytes()))
    assertThrows[ParsingException](a_boolean.parse(JsObj("a" -> BigDecimal(1.5)).toString.getBytes()))
    assertThrows[ParsingException](a_boolean.parse(JsObj("a" -> JsObj.empty).toString.getBytes()))
    assertThrows[ParsingException](a_boolean.parse(JsObj("a" -> JsArray.empty).toString.getBytes()))
    assert(a_boolean.parse(JsObj("a" -> true).toString.getBytes()) == JsObj("a" -> true))
    assert(a_boolean.parse(JsObj("a" -> false).toString.getBytes()) == JsObj("a" -> false))
    assert(a_null_or_boolean.parse(JsObj("a" -> JsNull).toString.getBytes()) == JsObj("a" -> JsNull))

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

    val parser = JsObjParser(JsObjSpec("a" -> array_of_value
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
