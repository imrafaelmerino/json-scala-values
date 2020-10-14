package value.specs

import org.scalatest.FlatSpec
import value.{InvalidJson, JsArray, JsBigDec, JsInt, JsLong, JsNull, JsObj, JsObjParser, JsStr, TRUE}
import value.Preamble._
import value.spec.Preamble._
import value.spec.JsArraySpecs._
import value.spec.JsBoolSpecs.{bool, isFalse, isTrue}
import value.spec.JsNumberSpecs._
import value.spec.JsObjSpecs._
import value.spec.JsSpecs.{any, anySuchThat}
import value.spec.JsStrSpecs.{str, strSuchThat}
import value.spec.{*, Invalid, JsArraySpec, JsArraySpecs, JsNumberSpecs, JsObjSpec, JsObjSpecs, Result, Valid}


class JsObjParserSpec extends FlatSpec
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
                    "h" -> BigInt(1000),
                    "i" -> 3.5d
                    )

    val spec = JsObjSpec("a" -> str,
                         "b" -> int,
                         "c" -> bool,
                         "d" -> decimal,
                         "e" -> number,
                         "f" -> decimal,
                         "g" -> long,
                         "h" -> integral,
                         "i" -> decimal
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes

                              )

    assert(either.contains(obj) &&
           either.exists(it => it.hashCode() == obj.hashCode())
           )
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
                                   ),
                    "i" -> null
                    )

    val spec = JsObjSpec("f" -> arrayOfInt,
                         "g" -> arrayOfInt(nullable = true),
                         "h" -> arrayOfInt(elemNullable = true),
                         "i" -> arrayOfInt(nullable = true,
                                           elemNullable = true
                                           )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes)

    assert(either.exists(it => obj == it && it.hashCode() == obj.hashCode()))


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

    val spec = JsObjSpec("f" -> arrayOfLong,
                         "g" -> arrayOfLong(nullable = true),
                         "h" -> arrayOfLong(elemNullable = true),
                         "i" -> arrayOfLong(nullable = true,
                                            elemNullable = true
                                            )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes

                              )

    assert(either.exists(it => obj == it && it.hashCode() == obj.hashCode()))

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

    val spec = JsObjSpec("f" -> arrayOfDecimal,
                         "g" -> arrayOfDecimal(nullable = true),
                         "h" -> arrayOfDecimal(elemNullable = true),
                         "i" -> arrayOfDecimal(nullable = true,
                                               elemNullable = true
                                               )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes
                              )

    assert(either.exists(it => obj == it && it.hashCode() == obj.hashCode()))

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

    val spec = JsObjSpec("f" -> arrayOfStr,
                         "g" -> arrayOfStr(nullable = true),
                         "h" -> arrayOfStr(elemNullable = true),
                         "i" -> arrayOfStr(nullable = true,
                                           elemNullable = true
                                           )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes
                              )

    assert(either.exists(it => obj == it && it.hashCode() == obj.hashCode()))

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

    val spec = JsObjSpec("f" -> arrayOfBool,
                         "g" -> arrayOfBool(nullable = true),
                         "h" -> arrayOfBool(elemNullable = true),
                         "i" -> arrayOfBool(nullable = true,
                                            elemNullable = true
                                            )

                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes

                              )

    assert(either.exists(it => obj == it && it.hashCode() == obj.hashCode()))

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

    val spec = JsObjSpec("f" -> arrayOfNumber,
                         "g" -> arrayOfNumber(nullable = true),
                         "h" -> arrayOfNumber(elemNullable = true),
                         "i" -> arrayOfNumber(nullable = true,
                                              elemNullable = true
                                              )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes,
                              )

    assert(either.exists(it => obj == it && it.hashCode() == obj.hashCode()))

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

    val spec = JsObjSpec("f" -> arrayOfIntegral,
                         "g" -> arrayOfIntegral(nullable = true),
                         "h" -> arrayOfIntegral(elemNullable = true),
                         "i" -> arrayOfIntegral(nullable = true,
                                                elemNullable = true
                                                )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes
                              )

    assert(either.exists(it => obj == it && it.hashCode() == obj.hashCode()))

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
                                                           "e" -> arrayOfInt
                                                           )
                                          ),
                         "j" -> arrayOf(JsObjSpec("a" -> int,
                                                  "b" -> str
                                                  )
                                        )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes
                              )

    assert(either.exists(it => obj == it && it.hashCode() == obj.hashCode()))

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
                         "b" -> array(elemNullable = true),
                         "c" -> array(nullable = true),
                         "d" -> array(elemNullable = true,
                                      nullable = true
                                      )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes
                              )

    assert(either.exists(it => obj == it && it.hashCode() == obj.hashCode()))

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

    val spec = JsObjSpec("a" -> arrayOfStr,
                         "b" -> arrayOfStr(elemNullable = true),
                         "c" -> arrayOfStr(nullable = true),
                         "d" -> arrayOfStr(elemNullable = true,
                                           nullable = true
                                           )
                         )

    def parser: JsObjParser = JsObjParser(spec)

    def either = parser.parse(obj.toString.getBytes
                              )

    assert(either.exists(it => obj == it && it.hashCode() == obj.hashCode()))

  }

  "parsing a key that doesn't match the int spec" should "fail if the element is not an integer" in
  {

    val a_int = JsObjParser(JsObjSpec("a" -> int
                                      )
                            )

    val a_int_or_null = JsObjParser(JsObjSpec("a" -> int(nullable = true)
                                              )
                                    )


    assert(a_int.parse(JsObj("a" -> true).toString.getBytes()

                       ).isLeft
           )
    assert(a_int.parse(JsObj("a" -> "123").toString.getBytes()

                       ).isLeft
           )
    assert(a_int.parse(JsObj("a" -> 1.5).toString.getBytes()

                       ).isLeft
           )
    assert(a_int.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()

                       ).isLeft
           )
    assert(a_int.parse(JsObj("a" -> BigDecimal.valueOf(1.5)).toString.getBytes()
                       ).isLeft
           )
    assert(a_int.parse(JsObj("a" -> JsNull).toString.getBytes()

                       ).isLeft
           )
    assert(a_int.parse(JsObj("a" -> "hi").toString.getBytes()

                       ).isLeft
           )
    assert(a_int.parse(JsObj("a" -> JsObj.empty).toString.getBytes()
                       ).isLeft
           )
    assert(a_int.parse(JsObj("a" -> JsArray.empty).toString.getBytes()

                       ).isLeft
           )
    assert(a_int.parse(JsObj("a" -> 10).toString.getBytes()

                       ).contains(JsObj("a" -> 10))
           )
    assert(a_int_or_null.parse(JsObj("a" -> JsNull).toString.getBytes()

                               ).contains(JsObj("a" -> JsNull))
           )

  }

  "parsing a key that doesn't match the long spec" should "fail if the element is not an long" in
  {

    val a_long = JsObjParser(JsObjSpec("a" -> long
                                       )
                             )

    val a_long_or_null = JsObjParser(JsObjSpec("a" -> long(nullable = true)))

    assert(a_long.parse(JsObj("a" -> true).toString.getBytes()

                        ).isLeft
           )
    assert(a_long.parse(JsObj("a" -> 1.5).toString.getBytes()

                        ).isLeft
           )
    assert(a_long.parse(JsObj("a" -> "10000").toString.getBytes()

                        ).isLeft
           )
    assert(a_long.parse(JsObj("a" -> BigDecimal.valueOf(1.5)).toString.getBytes()

                        ).isLeft
           )
    assert(a_long.parse(JsObj("a" -> JsNull).toString.getBytes()

                        ).isLeft
           )
    assert(a_long.parse(JsObj("a" -> "hi").toString.getBytes()

                        ).isLeft
           )
    assert(a_long.parse(JsObj("a" -> JsObj.empty).toString.getBytes()

                        ).isLeft
           )
    assert(a_long.parse(JsObj("a" -> JsArray.empty).toString.getBytes()

                        ).isLeft
           )
    assert(a_long.parse(JsObj("a" -> 10).toString.getBytes()

                        ).contains(JsObj("a" -> 10))
           )
    assert(a_long.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()
                        ).contains(JsObj("a" -> Long.MaxValue))
           )
    assert(a_long_or_null.parse(JsObj("a" -> JsNull).toString.getBytes()
                                ).contains(JsObj("a" -> JsNull))
           )

  }

  "parsing a key that doesn't match the decimal spec" should "fail if the element is not a number" in
  {

    val a_decimal = JsObjParser(JsObjSpec("a" -> decimal
                                          )
                                )

    val a_decimal_or_null = JsObjParser(JsObjSpec("a" -> decimal(nullable = true)
                                                  )
                                        )
    assert(a_decimal.parse(JsObj("a" -> true).toString.getBytes()

                           ).isLeft
           )
    assert(a_decimal.parse(JsObj("a" -> JsNull).toString.getBytes()

                           ).isLeft
           )
    assert(a_decimal.parse(JsObj("a" -> "1.50").toString.getBytes()

                           ).isLeft
           )
    assert(a_decimal.parse(JsObj("a" -> "hi").toString.getBytes(),

                           ).isLeft
           )
    assert(a_decimal.parse(JsObj("a" -> JsObj.empty).toString.getBytes()

                           ).isLeft
           )
    assert(a_decimal.parse(JsObj("a" -> JsArray.empty).toString.getBytes()

                           ).isLeft
           )
    assert(a_decimal.parse(JsObj("a" -> 10).toString.getBytes()

                           ).contains(JsObj("a" -> 10))
           )
    assert(a_decimal.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()

                           ).contains(JsObj("a" -> Long.MaxValue))
           )
    assert(a_decimal.parse(JsObj("a" -> 1.5).toString.getBytes()

                           ).contains(JsObj("a" -> 1.5))
           )
    assert(a_decimal.parse(JsObj("a" -> BigInt("10000000000000")).toString.getBytes()

                           ).contains(JsObj("a" -> BigInt("10000000000000")))
           )
    assert(a_decimal_or_null.parse(JsObj("a" -> JsNull).toString.getBytes(),
                                   ).contains(JsObj("a" -> JsNull))
           )

  }

  "parsing a key that doesn't match the integral spec" should "fail if the element is not an integral number" in
  {

    val a_integral = JsObjParser(JsObjSpec("a" -> integral
                                           )
                                 )

    val a_integral_or_null = JsObjParser(JsObjSpec("a" -> JsNumberSpecs.integral(nullable = true)
                                                   )
                                         )
    assert(a_integral.parse(JsObj("a" -> true).toString.getBytes()
                            ).isLeft
           )

    assert(a_integral.parse(JsObj("a" -> JsNull).toString.getBytes()
                            ).isLeft
           )
    assert(a_integral.parse(JsObj("a" -> "10000").toString.getBytes()
                            ).isLeft
           )
    assert(a_integral.parse(JsObj("a" -> 1.5).toString.getBytes()
                            ).isLeft
           )
    assert(a_integral.parse(JsObj("a" -> "hi").toString.getBytes()

                            ).isLeft
           )
    assert(a_integral.parse(JsObj("a" -> JsObj.empty).toString.getBytes()

                            ).isLeft
           )
    assert(a_integral.parse(JsObj("a" -> JsArray.empty).toString.getBytes()

                            ).isLeft
           )
    assert(a_integral.parse(JsObj("a" -> 10).toString.getBytes()

                            ).contains(JsObj("a" -> 10))
           )
    assert(a_integral.parse(JsObj("a" -> Long.MaxValue).toString.getBytes()

                            ).contains(JsObj("a" -> Long.MaxValue))
           )
    assert(a_integral.parse(JsObj("a" -> BigInt("10000000000000")).toString.getBytes()
                            ).contains(JsObj("a" -> BigInt("10000000000000")))
           )
    assert(a_integral_or_null.parse(JsObj("a" -> JsNull).toString.getBytes()

                                    ).contains(JsObj("a" -> JsNull))
           )

  }

  "parsing a key that doesn't match the string spec" should "fail if the element is not a string" in
  {

    val a_string = JsObjParser(JsObjSpec("a" -> str
                                         )
                               )

    val a_null_or_string = JsObjParser(JsObjSpec("a" -> str(nullable = true)
                                                 )
                                       )
    assert(a_string.parse(JsObj("a" -> true).toString.getBytes()

                          ).isLeft
           )
    assert(a_string.parse(JsObj("a" -> JsNull).toString.getBytes()

                          ).isLeft
           )
    assert(a_string.parse(JsObj("a" -> 1.5).toString.getBytes()

                          ).isLeft
           )
    assert(a_string.parse(JsObj("a" -> 100).toString.getBytes()

                          ).isLeft
           )
    assert(a_string.parse(JsObj("a" -> BigDecimal(1.5)).toString.getBytes()

                          ).isLeft
           )
    assert(a_string.parse(JsObj("a" -> JsObj.empty).toString.getBytes()

                          ).isLeft
           )
    assert(a_string.parse(JsObj("a" -> JsArray.empty).toString.getBytes()

                          ).isLeft
           )
    assert(a_string.parse(JsObj("a" -> "hi").toString.getBytes()

                          ).contains(JsObj("a" -> "hi"))
           )
    assert(a_null_or_string.parse(JsObj("a" -> JsNull).toString.getBytes()

                                  ).contains(JsObj("a" -> JsNull))
           )

  }

  "parsing a key that doesn't match the bool spec" should "fail if the element is not a boolean" in
  {

    val a_boolean = JsObjParser(JsObjSpec("a" -> bool
                                          )
                                )

    val a_null_or_boolean = JsObjParser(JsObjSpec("a" -> bool(nullable = true)
                                                  )
                                        )
    assert(a_boolean.parse(JsObj("a" -> "true").toString.getBytes()

                           ).isLeft
           )
    assert(a_boolean.parse(JsObj("a" -> "false").toString.getBytes()

                           ).isLeft
           )
    assert(a_boolean.parse(JsObj("a" -> JsNull).toString.getBytes()

                           ).isLeft
           )
    assert(a_boolean.parse(JsObj("a" -> 1.5).toString.getBytes()

                           ).isLeft
           )
    assert(a_boolean.parse(JsObj("a" -> 100).toString.getBytes()

                           ).isLeft
           )
    assert(a_boolean.parse(JsObj("a" -> BigDecimal(1.5)).toString.getBytes()).isLeft
           )
    assert(a_boolean.parse(JsObj("a" -> JsObj.empty).toString.getBytes()
                           ).isLeft
           )
    assert(a_boolean.parse(JsObj("a" -> JsArray.empty).toString.getBytes()
                           ).isLeft
           )
    assert(a_boolean.parse(JsObj("a" -> true).toString.getBytes()

                           ).contains(JsObj("a" -> true))
           )
    assert(a_boolean.parse(JsObj("a" -> false).toString.getBytes()

                           ).contains(JsObj("a" -> false))
           )
    assert(a_null_or_boolean.parse(JsObj("a" -> JsNull).toString.getBytes()

                                   ).contains(JsObj("a" -> JsNull))
           )

  }

  "parsing a key that doesn't match the object spec" should "fail if the element is not an object" in
  {


    val parser = JsObjParser(JsObjSpec("a" -> obj
                                       )
                             )
    assert(parser.parse(JsObj("a" -> "hi").toString.getBytes()
                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> false).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> JsNull).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> 1.5).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> 100).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> BigDecimal(1.5)).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> JsArray.empty).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> JsObj.empty).toString.getBytes()

                        ).contains(JsObj("a" -> JsObj.empty))
           )

  }

  "parsing a key that doesn't match the array spec" should "fail if the element is not an array" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> array
                                       )
                             )
    assert(parser.parse(JsObj("a" -> "hi").toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> false).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> JsNull).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> 1.5).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> 100).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> BigDecimal(1.5)).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> JsObj.empty).toString.getBytes()

                        ).isLeft
           )
    assert(parser.parse(JsObj("a" -> JsArray.empty).toString.getBytes()

                        ).contains(JsObj("a" -> JsArray.empty))
           )

  }

  "parsing a complex object with its spec" should "deserialize the string into the right Json Object" in
  {

    val obj = "\n{\n  \"a\" : {\n    \"b\": 1,\n    \"c\": [1,2,3,4,5,6,7],\n    \"d\": [\"a\",\"b\",\"c\",\"d\",\"e\"],\n    \"e\": true,\n    \"f\": {\n      \"g\": \"hi\",\n      \"h\": {\n        \"i\": [{\"a\": 1,\"b\": \"bye\"},{\"a\": 4,\"b\": \"hi\"}]\n      },\n      \"j\": [1.3,1.5,2.5,10.0],\n      \"k\": {\"l\": false,\"m\": \"red\",\"n\": 1.5}\n    }\n  }\n}"

    def parsedWithoutSpec = JsObjParser.parse(obj)

    val spec = JsObjSpec("a" -> JsObjSpec("b" -> int,
                                          "c" -> arrayOfInt,
                                          "d" -> arrayOfStr,
                                          "e" -> bool,
                                          "f" -> JsObjSpec("g" -> str,
                                                           "h" -> JsObjSpec("i" -> arrayOf(JsObjSpec("a" -> int,
                                                                                                     "b" -> str
                                                                                                     )
                                                                                           )
                                                                            ),
                                                           "j" -> arrayOfDecimal
                                                           ,
                                                           "k" -> JsObjSpec("l" -> bool,
                                                                            "m" -> str,
                                                                            "n" -> decimal
                                                                            )
                                                           )


                                          )
                         )

    val parser = JsObjParser(spec)
    val parsedObj = parser.parse(obj.getBytes

                                 )

    assert(parsedWithoutSpec == parsedObj)

  }


  "given an object that conforms a spec" should "no error must be returned" in
  {
    def greaterOrEqualThan(value: Int): Int => Result = i => if (i >= value) Valid else Invalid(s"minimum $value")

    def interval(min: BigDecimal,
                 max: BigDecimal
                ): BigDecimal => Result =
      (d: BigDecimal) => if (d <= max && d >= min) Valid else Invalid(s"Not between [$min,$max]")

    val json_str = "{\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"age\": 21,\n  \"latitude\": 48.858093,\n  \"longitude\": 2.294694,\n  \"fruits\": [\n    \"apple\",\n    \"orange\",\n    \"pear\"\n  ],\n  \"numbers\": [\n    1,\n    2,\n    3,\n    4,\n    5,\n    6,\n    7,\n    8,\n    9,\n    10\n  ],\n  \"vegetables\": [\n    {\n      \"veggieName\": \"potato\",\n      " +
                   "\"veggieLike\": true\n    },\n    {\n      \"veggieName\": \"broccoli\",\n      \"veggieLike\": false\n    }\n  ]\n}\n      " +
                   "\"veggieName\": \"broccoli\",\n      \"veggieLike\": false\n    }\n  ]\n}"

    val json_bytes = json_str.getBytes

    val spec = JsObjSpec("firstName" -> str,
                         "lastName" -> str,
                         "age" -> intSuchThat(greaterOrEqualThan(0)),
                         "latitude" -> decimalSuchThat(interval(-90,
                                                                90
                                                                )
                                                       ),
                         "longitude" -> decimalSuchThat(interval(-180,
                                                                 180
                                                                 )
                                                        ),
                         "fruits" -> arrayOfStr,
                         "numbers" -> arrayOfInt,
                         "vegetables" -> arrayOf(JsObjSpec("veggieName" -> str,
                                                           "veggieLike" -> bool
                                                           )
                                                 )
                         )

    val parser = JsObjParser(spec)

    assert(JsObjParser.parse(json_str) == parser.parse(json_bytes))

    assert(JsObjParser.parse(json_str)
             .map(it => it.validate(spec).isEmpty)
             .getOrElse(false)
           )

  }

  "parsing a valid json" should "return no error" in
  {


    val spec = JsObjSpec(
      "a" -> longSuchThat(i => if (i % 2 == 0) Valid else Invalid("odd number")),
      "a1" -> longSuchThat(i => if (i % 2 == 0) Valid else Invalid("odd number"),
                           nullable = true
                           ),
      "b" -> intSuchThat(i => if (i % 2 != 0) Valid else Invalid("even number")),
      "b1" -> intSuchThat(i => if (i % 2 != 0) Valid else Invalid("even number"),
                          nullable = true
                          ),
      "c" -> strSuchThat(s => if (s.length < 3) Valid else Invalid("too long")),
      "c1" -> strSuchThat(s => if (s.length < 3) Valid else Invalid("too long"),
                          nullable = true
                          ),
      "d" -> arrayOfIntSuchThat(a => if (a.head == JsInt(-1)) Valid else Invalid("first not one"),
                                elemNullable = true
                                ),
      "e" -> arrayOfStrSuchThat(a => if (a.head == JsStr("a")) Valid else Invalid("first not a"),
                                elemNullable = true
                                ),
      "f" -> arrayOfLongSuchThat(a => if (a.head == JsLong(-1)) Valid else Invalid("first not 1"),
                                 elemNullable = true
                                 ),
      "g" -> arrayOfNumberSuchThat(a => if (a.head == JsBigDec(-1.10E3)) Valid else Invalid("first not 1.10"),
                                   elemNullable = true
                                   ),
      "h" -> arrayOfDecimalSuchThat(a => if (a.head == JsBigDec(5.1110)) Valid else Invalid("first not 5.1110"),
                                    elemNullable = true
                                    ),
      "i" -> arrayOfBoolSuchThat(a => if (a.head == TRUE) Valid else Invalid("first not true"),
                                 elemNullable = true
                                 ),
      "j" -> JsObjSpecs.conforms(JsObjSpec("a" -> int,
                                           "b" -> str
                                           ),
                                 nullable = true
                                 ),
      "k" -> objSuchThat(o => if (o.containsKey("a")) Valid else Invalid("no contains a")),
      "l" -> objSuchThat(o => if (o.containsKey("a")) Valid else Invalid("no contains a"),
                         nullable = true
                         ),
      "m" -> JsArraySpecs.conforms(JsArraySpec(str,
                                               int
                                               ),
                                   nullable = true
                                   ),
      "n" -> JsArraySpecs.conforms(JsArraySpec(str,
                                               int
                                               )
                                   ),
      "o" -> isTrue,
      "p" -> isFalse,
      "q" -> isFalse(nullable = true),
      "r" -> isTrue(nullable = true),
      "s" -> decimal(nullable = true),
      "t" -> JsNumberSpecs.integral,
      "u" -> JsNumberSpecs.integral(nullable = true),
      "v" -> JsNumberSpecs.integralSuchThat(i => if (i == 0) Valid else Invalid("not 0")),
      "w" -> JsNumberSpecs.integralSuchThat(i => if (i == 0) Valid else Invalid("not 0"),
                                            nullable = true
                                            ),
      "x" -> JsNumberSpecs.decimalSuchThat(i => if (i > 0.5) Valid else Invalid("not greater than 0.5"),
                                           nullable = true
                                           ),
      "y" -> JsNumberSpecs.numberSuchThat(i => if (i.isIntegral) Valid else Invalid("not integer")),
      "z" -> JsNumberSpecs.numberSuchThat(i =>
                                            if (i.isIntegral) Valid else Invalid("not integer"),
                                          nullable = true
                                          ),
      )

    val o = JsObj("a" -> 2,
                  "a1" -> JsNull,
                  "b" -> 3,
                  "b1" -> JsNull,
                  "c" -> "hi",
                  "c1" -> JsNull,
                  "d" -> JsArray(-1,
                                 -2,
                                 -3,
                                 JsNull
                                 ),
                  "e" -> JsArray("a",
                                 "b",
                                 "c",
                                 JsNull
                                 ),
                  "f" -> JsArray(-1L,
                                 -2L,
                                 -3L,
                                 Long.MaxValue,
                                 JsNull
                                 ),
                  "g" -> JsArray(-1.10E3,
                                 2E-3,
                                 3E-2,
                                 JsNull
                                 ),
                  "h" -> JsArray(5.1110,
                                 -2.0E8,
                                 -3.0,
                                 10.0E10,
                                 1111111111111111111.0,
                                 JsNull
                                 ),
                  "i" -> JsArray(true,
                                 false,
                                 false,
                                 JsNull
                                 ),
                  "j" -> JsNull,
                  "k" -> JsObj("a" -> true,
                               "b" -> false
                               ),
                  "l" -> JsNull,
                  "m" -> JsNull,
                  "n" -> JsArray("hi",
                                 1
                                 ),
                  "o" -> true,
                  "p" -> false,
                  "q" -> JsNull,
                  "r" -> JsNull,
                  "s" -> JsNull,
                  "t" -> 12,
                  "u" -> JsNull,
                  "v" -> 0,
                  "w" -> JsNull,
                  "x" -> JsNull,
                  "y" -> 1,
                  "z" -> JsNull

                  )

    val parser = JsObjParser(spec)
    assert(o.validate(spec).isEmpty)
    assert(parser.parse(o.toString

                        ) == Right(o)
           )
  }

  "" should "" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> 1,
                                       "b" -> Long.MaxValue,
                                       "c" -> BigDecimal(1.5),
                                       "d" -> true,
                                       "e" -> false,
                                       "f" -> Long.MaxValue,
                                       "g" -> "a"
                                       )
                             )

    val obj = JsObj("a" -> 1,
                    "b" -> Long.MaxValue,
                    "c" -> BigDecimal(1.5),
                    "d" -> true,
                    "e" -> false,
                    "f" -> Long.MaxValue,
                    "g" -> "a"
                    )

    assert(parser.parse(obj.toPrettyString) == Right(obj))


  }

  "required flag to false" should "not fail if the field is missing" in
  {
    val spec = JsObjSpec("a" -> str(required = false),
                         "b" -> int(required = false
                                    ),
                         "c" -> long(required = false
                                     ),
                         "d" -> decimal(required = false
                                        ),
                         "e" -> integral(required = false
                                         ),
                         "f" -> JsObjSpecs.obj(required = false),
                         * -> any
                         )

    val parser = JsObjParser(spec)

    val obj = JsObj("g" -> 1,
                    "h" -> 3,
                    "i" -> 4,
                    "j" -> 3.5
                    )

    assert(parser.parse(obj.toPrettyString) == Right(obj))

  }

  "suchThat predicates" should "test the parsed value" in
  {

    def parser =
    {
      val intGreaterThan0 = intSuchThat(i => if (i > 0) Valid else Invalid("must be greater than 0"),
                                        nullable = true
                                        )
      val strStartsWithA = strSuchThat(s => if (s.startsWith("a")) Valid else Invalid("must start with a"),
                                       nullable = true
                                       )
      val decimalGreaterThanZero = decimalSuchThat(i => if (i > 0) Valid else Invalid("must be greater than 0"),
                                                   nullable = true
                                                   )
      val longGreaterThanZero = longSuchThat(i => if (i > 0) Valid else Invalid("must be greater than 0"),
                                             nullable = true
                                             )
      val integralGreaterThanZero = integralSuchThat(i => if (i > 0) Valid else Invalid("must be greater than 0"),
                                                     nullable = true
                                                     )
      val objNonEmpty = objSuchThat(o => if (o.isNotEmpty) Valid else Invalid("is empty"),
                                    nullable = true
                                    )
      JsObjParser(JsObjSpec(
        "a" -> intGreaterThan0,
        "b" -> strStartsWithA,
        "c" -> arrayOfIntegralSuchThat(a => if (a.isNotEmpty) Valid else Invalid("must not be empty"),
                                       nullable = true
                                       ),
        "d" -> any,
        "e" -> anySuchThat(v => if (v.isLong) Valid else Invalid("int")),
        "f" -> longGreaterThanZero,
        "g" -> intGreaterThan0,
        "h" -> strStartsWithA,
        "i" -> decimalGreaterThanZero,
        "j" -> decimalGreaterThanZero,
        "k" -> longGreaterThanZero,
        "l" -> integralGreaterThanZero,
        "m" -> integralGreaterThanZero,
        "n" -> objNonEmpty,
        "o" -> objNonEmpty
        )
                  )
    }

    val either: Either[InvalidJson, JsObj] = parser.parse("{\n  \"o\": {\"a\": true},\n  \"n\":null,\n \"m\":1111111111111111111111111111111111111111,\"l\":null,\"k\":null,\"j\":1.1,\"i\":null,\"h\":null,\"g\":null, \"a\": 1,\n  \"b\": \"a\",\n  \"c\": [\n    1,\n    10\n  ],\n  \"d\": true,\n  \"e\": 1,\n  \"f\": 1}")

    assert(either.isRight)


  }

  "array nullable and with null specs" should "not fail" in
  {

    val arrayOfIntGT0 = arrayOfTestedInt(i => if (i > 0) Valid else Invalid(""),
                                         nullable = true,
                                         elemNullable = true
                                         )

    val arrayOfLongGT0 = arrayOfTestedLong(i => if (i > 0) Valid else Invalid(""),
                                           nullable = true,
                                           elemNullable = true
                                           )

    val arrayOfStrSWa = arrayOfTestedStr(i => if (i.startsWith("a")) Valid else Invalid(""),
                                         nullable = true,
                                         elemNullable = true
                                         )

    val arrayOfDecGT0 = arrayOfTestedDecimal(i => if (i > 0) Valid else Invalid(""),
                                             nullable = true,
                                             elemNullable = true
                                             )

    val arrayOfIntegralGT0 = arrayOfTestedIntegral(i => if (i > 0) Valid else Invalid(""),
                                                   nullable = true,
                                                   elemNullable = true
                                                   )
    val arrayOfObjNotEmpty = arrayOfTestedObj(o => if(o.isNotEmpty)Valid else Invalid("empty obj"),
                                              nullable = true,
                                              elemNullable = true
                                              )

    val arrayOfNumberIsBigInt = arrayOfTestedNumber(o => if(o.isBigDec)Valid else Invalid("not bigint"),
                                                    nullable = true,
                                                    elemNullable = true
                                                    )
    val parser = JsObjParser(JsObjSpec("a" -> arrayOfIntGT0,
                                       "b" -> arrayOfIntGT0,
                                       "c" -> arrayOfIntGT0,
                                       "d" -> arrayOfLongGT0,
                                       "e" -> arrayOfLongGT0,
                                       "f" -> arrayOfLongGT0,
                                       "g" -> arrayOfStrSWa,
                                       "h" -> arrayOfStrSWa,
                                       "i" -> arrayOfStrSWa,
                                       "j" -> arrayOfDecGT0,
                                       "k" -> arrayOfDecGT0,
                                       "l" -> arrayOfDecGT0,
                                       "m" -> arrayOfIntegralGT0,
                                       "n" -> arrayOfIntegralGT0,
                                       "o" -> arrayOfIntegralGT0,
                                       "p" -> arrayOfObjNotEmpty,
                                       "q" -> arrayOfObjNotEmpty,
                                       "r" -> arrayOfObjNotEmpty,
                                       "s" -> arrayOfNumberIsBigInt,
                                       "t" -> arrayOfNumberIsBigInt,
                                       "u" -> arrayOfNumberIsBigInt,
                                       * -> any
                                       )
                             )

    val either = parser.parse("{\n  \"a\": null,\n  \"b\": [1,null],\n  \"c\": [1,2],\n  \"d\": null,\n  \"e\": [1,null],\n  \"f\": [1,2],\n  \"g\": null,\n  \"h\": [\"a\",null],\n  \"i\": [\"a\",\"ab\"],\n  \"j\": null,\n  \"k\": [1.3,null],\n  \"l\": [1.2,1.3],\n  \"m\": null,\n  \"n\": [199999999999999999999999,null],\n  \"o\": [199999999999999999999999,199999999999999999999999],\n  " +
                              "\"p\": null,\n  \"q\": [null,{\"a\": 1},{\"b\": 2}],\n  \"r\": [{\"a\": 1},{\"b\": 2}],\n  \"s\": null,\n  \"t\": [19999999999999999999999999999999999999999999999999999999999999999,null],\n  \"u\": [19999999999999999999999999999999999999999999999999999999999999999,19999999999999999999999999999999999999999999999999999999999999999]\n}")

    assert(either.isRight)
  }

  "array of obj specs" should "not fail" in
  {

    val parser = JsObjParser(JsObjSpec("a" -> arrayOfObj,
                                       "b" -> arrayOfObj(nullable = true),
                                       "c" -> arrayOfObj(elemNullable = true),
                                       "d" -> arrayOfObj(nullable = true,elemNullable = true),
                                       )
                             )

    val json = "{\n  \"a\": [{\"b\": 1},{\"c\": 2}],\n  \"b\": null,\n  \"c\": [{\"b\": 1},null],\n  \"d\": [{\"b\": 1},null]\n}"

    assert(parser.parse(json).isRight)
  }

}
